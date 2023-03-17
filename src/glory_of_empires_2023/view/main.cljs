(ns glory-of-empires-2023.view.main
  (:require
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.aws.dynamo-db :as dynamo-db]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.login :as login]
    [glory-of-empires-2023.view.choose-system :as choose-system]
    [glory-of-empires-2023.view.add-ships :as add-ships]
    [glory-of-empires-2023.subs :as subs]))

(defn race-selector []
  (let [current-player @(subscribe [::subs/current-player])
        players (vals @(subscribe [::subs/players-amended]))]
    [:select {:value (name current-player)
              :on-change #(dispatch [::change-player (-> % .-target .-value)])}
     (for [{id :id, player-name :name} players]
       ^{:key id} [:option {:value (name id)} player-name])]))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [choose-system/view]
    :add-ships [add-ships/view]
    nil))

(defn game-panel []
  [:div
   [dialog]
   [:div "Current Player" [race-selector]]
   [:div [:button {:on-click #(dispatch [::fetch-game]) } "GET GAME"]]
   [board/view]])

(defn main-panel []
  (let [logged-in? @(subscribe [::subs/login])]
    (if logged-in?
      [game-panel]
      [login/view])))

;; events

(reg-event-db ::change-player
  (fn [db [_ player]]
    (assoc db :current-player (keyword player))))

(reg-event-db ::fetch-game [debug/log-event]
  (fn [db _]
    (let [game-id "38462387647832647"]
      (dynamo-db/get-game db game-id
        (fn [game] (dispatch [::game-received game])))
      (assoc db :fetching game-id))))

(reg-event-db ::game-received [debug/log-event]
  (fn [db [_ game]]
    (log ::game-received)
    (assoc db :game game)))