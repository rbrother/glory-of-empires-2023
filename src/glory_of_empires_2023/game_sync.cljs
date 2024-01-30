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

(defn update-game [{db :db} fn]
  {:db (update db :game fn)
   :dispatch [::save-game]})

(defn create-websocket []
  (ws/create sync-api-url
             {:on-open #(dispatch [::websocket-opened])
              :on-close #(dispatch [::websocket-closed])
              :on-error (fn [] (log "WS error"))
              :on-message #(dispatch [::websocket-message-received (.-data %)])}))

(reg-event-fx ::websocket-message-received [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ data-str]]
    (let [data (js->clj (js/JSON.parse data-str) :keywordize-keys true)]
      (log {:message-received data})
      {:db db
       :dispatch [::update-game-from-server]})))

(reg-event-db ::websocket-opened [debug/log-event debug/validate-malli]
  (fn [db _] db))

(reg-event-fx ::websocket-closed [debug/log-event debug/validate-malli]
  (fn [{db :db} _]
    {:db db
     :dispatch-later {:ms 1000 :dispatch [::create-websocket]}}))

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
             (dissoc :fetching))}))

(reg-event-fx ::game-saved [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ saved-game _result]]
    {:db (-> db
             (dissoc :fetching)
             (assoc :game-db saved-game))
     :dispatch [::send-websocket-message {:current-player (get-in db [:game :current-player])}]}))

(reg-event-fx ::game-update-received [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} [_ remote-game]]
    (if (= game game-db) ;; No local changes?
      {:db db, :dispatch [::game-received remote-game]}
      (let [local-diff (edit/diff game-db game)
            merged (try
                     (edit/patch remote-game local-diff)
                     (catch js/Error e
                       (log-error e)
                       ;; If application of local changes fails because
                       ;; of merge conflict, ignore local changes
                       remote-game))]
        (log {:merge-changes (edit/get-edits local-diff)})
        {:db (assoc db
               :game-db remote-game
               :game (assoc merged :version (:version remote-game)))
         :dispatch [::save-game] ;; immediately try again saving
         }))))

(reg-event-fx ::update-game-from-server
  (fn [{{:keys [game] :as db} :db} _]
    (dynamo-db/get-game db (:id game)
                        (fn [game] (dispatch [::game-update-received game])))
    {:db (assoc db :fetching game-id)}))

(reg-event-fx ::save-game [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} _]
    (let [new-game (update game :version inc)]
      (dynamo-db/save-game db new-game (:version game-db)
                           #(dispatch [::game-saved new-game %])
                           #(dispatch [::update-game-from-server]))
      {:db (assoc db
             :fetching game-id
             :game new-game)})))
