(ns glory-of-empires-2023.game-sync
  (:require
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx]]
    [glory-of-empires-2023.aws.dynamo-db :as dynamo-db]))

(def game-id "Battle of Titans") ;; TODO: Allow multiple games

(reg-event-db ::fetch-game [debug/log-event debug/validate-malli]
  (fn [db _]
    (let []
      (dynamo-db/get-game db game-id
        (fn [game] (dispatch [::game-received game])))
      (assoc db :fetching game-id))))

(reg-event-fx ::game-received [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ game]]
    (log ::game-received)
    (log game)
    {:db (-> db
           (assoc :game game
             :game-db game)
           (dissoc :fetching))
     :dispatch-later {:ms 5000 :dispatch [::sync-game]}}))

(reg-event-fx ::game-saved [debug/log-event debug/validate-malli]
  (fn [{db :db} [_ saved-game result]]
    (log "game-saved")
    (log result)
    (log saved-game)
    {:db (-> db (dissoc :fetching)
           (assoc :game-db saved-game))
     :dispatch-later {:ms 5000 :dispatch [::sync-game]}}))

(reg-event-fx ::game-version-received [debug/log-event debug/validate-malli]
  (fn [{{{local-version :version id :id} :game :as db} :db} [_ {remote-version :version}]]
    (log [:game-version-received {:local local-version :remote remote-version}])
    (if (> remote-version local-version)
      (do
        (dynamo-db/get-game db id #(dispatch [::game-received %]))
        {:db (assoc db :fetching game-id)})
      {:db db
       :dispatch-later {:ms 5000 :dispatch [::sync-game]}})))

(reg-event-fx ::sync-game [debug/log-event debug/validate-malli]
  (fn [{{:keys [game game-db] :as db} :db} _]
    (if (= game game-db)
      (do
        (dynamo-db/get-game-version db (:id game) #(dispatch [::game-version-received %]))
        {:db db})
      (let [new-game (update game :version inc)]
        (dynamo-db/save-game db new-game (:version game-db)
          #(dispatch [::game-saved new-game %]))
        {:db (assoc db
               :fetching game-id
               :game new-game)}))))
