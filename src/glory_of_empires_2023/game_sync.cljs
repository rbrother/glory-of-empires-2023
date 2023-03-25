(ns glory-of-empires-2023.game-sync
  (:require
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx]]
    [glory-of-empires-2023.aws.dynamo-db :as dynamo-db]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.login :as login]
    [glory-of-empires-2023.view.choose-system :as choose-system]
    [glory-of-empires-2023.view.add-ships :as add-ships]
    [glory-of-empires-2023.subs :as subs]))

(def game-id "Battle of Titans") ;; TODO: Allow multiple games

(reg-event-db ::fetch-game [debug/log-event debug/validate-malli]
  (fn [db _]
    (let []
      (dynamo-db/get-game db game-id
        (fn [game] (dispatch [::game-received game])))
      (assoc db :fetching game-id))))

(reg-event-db ::game-received [debug/log-event debug/validate-malli]
  (fn [db [_ game]]
    (log ::game-received)
    (log game)
    (-> db
      (assoc :game game)
      (dissoc :fetching))))

(reg-event-db ::save-game [debug/log-event debug/validate-malli]
  (fn [{:keys [game] :as db} _]
    (dynamo-db/save-game db game #(dispatch [::game-saved %]))
    (assoc db :fetching game-id)))

(reg-event-db ::game-saved [debug/log-event debug/validate-malli]
  (fn [db [_ result]]
    (log ::game-saved)
    (log result)
    (dissoc db :fetching)))

(reg-event-fx ::sync-game
  (fn [{db :db} _]

    {:db db}
    ))
