(ns glory-of-empires-2023.view.main
  (:require
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.login :as login]
    [glory-of-empires-2023.view.choose-system :as choose-system]
    [glory-of-empires-2023.view.add-ships :as add-ships]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.game-sync :as game-sync]))

(defn race-selector []
  (let [current-player @(subscribe [::subs/current-player])
        players (vals @(subscribe [::subs/players-amended]))]
    (if current-player
      [:select {:value (name current-player)
                :on-change #(dispatch [::change-player (-> % .-target .-value)])}
       (for [{id :id, player-name :name} players]
         ^{:key id} [:option {:value (name id)} player-name])])))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [choose-system/view]
    :add-ships [add-ships/view]
    nil))

(defn game-panel []
  [:div
   [dialog]
   [:div "Current Player" [race-selector]]
   [:div
    [:button {:on-click #(dispatch [::game-sync/fetch-game]) } "GET GAME"]
    [:button {:on-click #(dispatch [::game-sync/save-game]) } "SAVE GAME"]]
   [board/view]])

(defn main-panel []
  (let [logged-in? @(subscribe [::subs/login])]
    (if logged-in?
      [game-panel]
      [login/view])))

;; events

(reg-event-db ::change-player [debug/log-event debug/validate-malli]
  (fn [db [_ player]]
    (assoc-in db [:game :current-player] (keyword player))))
