(ns glory-of-empires-2023.game-sync
  (:require
    [editscript.core :as edit]
    [wscljs.client :as ws]
    [wscljs.format]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx]]
    [glory-of-empires-2023.debug :as debug :refer [log log-error]]
    [glory-of-empires-2023.aws.dynamo-db :as dynamo-db]))

(def game-id "Battle of Titans") ;; TODO: Allow multiple games

(def sync-api-url "wss://57vx0wr6dj.execute-api.eu-north-1.amazonaws.com/Prod")

(def sync-later {:ms 7000 :dispatch [::sync-game]})

(defn create-websocket []
  (ws/create  sync-api-url
    {:on-open #(dispatch [::websocket-opened])
     :on-close #(dispatch [::websocket-closed])
     :on-error (fn [] (log "WS error"))
     :on-message #(dispatch [::websocket-message-received (.-data %)])}))

(reg-event-fx ::websocket-message-received [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ data-str]]
    (let [data (js->clj (js/JSON.parse data-str) :keywordize-keys true)]
      (log {:message-received data})
      {:db db})))

(reg-event-db ::websocket-opened [debug/log-event debug/validate-malli]
  (fn [db _] db))

(reg-event-fx ::websocket-closed [debug/log-event debug/validate-malli]
  (fn [{db :db} _]
    {:db db
     :dispatch-later [:ms 1000 :dispatch [::create-websocket]]}))

(reg-event-db ::create-websocket
  (fn [db _]
    (assoc-in db [:login :websocket]
      (create-websocket))))

(reg-event-db ::send-websocket-message [debug/log-event debug/validate-malli]
  (fn [db [_ message]]
    (let [ws (get-in db [:login :websocket])]
      (ws/send ws (assoc message :action "sendmessage")
        wscljs.format/json))
    db))

(reg-event-db ::fetch-game [debug/log-event debug/validate-malli]
  (fn [db _]
    (dynamo-db/get-game db game-id
      (fn [game] (dispatch [::game-received game])))
    (assoc db :fetching game-id)))

(reg-event-fx ::game-received [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ game]]
    {:db (-> db
           (assoc :game game
             :game-db game)
           (dissoc :fetching))
     :dispatch-later sync-later}))

(reg-event-fx ::game-saved [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ saved-game _result]]
    {:db (-> db
           (dissoc :fetching)
           (assoc :game-db saved-game))
     :dispatch [::send-websocket-message {:current-player (get-in db [:game :current-player])}]
     :dispatch-later sync-later}))

(reg-event-fx ::game-version-received [debug/log-event debug/validate-malli]
  (fn [{{{local-version :version id :id} :game :as db} :db} [_ {remote-version :version}]]
    (log [:game-version-received {:local local-version :remote remote-version}])
    (if (> remote-version local-version)
      (do
        (dynamo-db/get-game db id #(dispatch [::game-received %]))
        {:db (assoc db :fetching game-id)})
      {:db db
       :dispatch-later sync-later})))

(reg-event-fx ::merge-remote [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} [_ remote-game]]
    (let [local-diff (edit/diff game-db game)
          merged (try
                   (edit/patch remote-game local-diff)
                   (catch js/Error e
                     (log-error e)
                     ;; If application of local changes fails because
                     ;; of merge conflict, ignore local changes
                     remote-game))]
      (log (edit/get-edits local-diff))
      {:db (assoc db
             :game-db remote-game
             :game (assoc merged :version (:version remote-game)))
       :dispatch [::sync-game] ;; immediately try again saving
       })))

(reg-event-fx ::sync-game [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} _]
    (if (= game game-db)
      {:db db
       :dispatch-later sync-later}
      (let [new-game (update game :version inc)]
        (dynamo-db/save-game db new-game (:version game-db)
          #(dispatch [::game-saved new-game %])
          (fn [] ;; Remote is newer
            (dynamo-db/get-game db (:id game)
              (fn [game] (dispatch [::merge-remote game])))
            (assoc db :fetching game-id)))
        {:db (assoc db
               :fetching game-id
               :game new-game)}))))
