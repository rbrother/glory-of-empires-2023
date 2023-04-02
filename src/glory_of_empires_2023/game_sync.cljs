(ns glory-of-empires-2023.game-sync
  (:require
    [editscript.core :as edit]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx]]
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [glory-of-empires-2023.aws.dynamo-db :as dynamo-db]))

(def game-id "Battle of Titans") ;; TODO: Allow multiple games

(def sync-later {:ms 7000 :dispatch [::sync-game]})

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
    {:db (-> db (dissoc :fetching)
           (assoc :game-db saved-game))
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
          merged (edit/patch remote-game local-diff)]
      (log (edit/get-edits local-diff))
      {:db (assoc db
             :game-db remote-game
             :game (assoc merged :version (:version remote-game)))
       :dispatch [::sync-game] ;; immediately try again saving
       })))

(reg-event-fx ::sync-game [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} _]
    (if (= game game-db)
      (do
        (dynamo-db/get-game-version db (:id game) #(dispatch [::game-version-received %]))
        {:db db})
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
