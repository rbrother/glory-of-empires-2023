(ns glory-of-empires-2023.view.main
  (:require
    [glory-of-empires-2023.debug :as debug :refer [log]]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx]]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.login :as login]
    [glory-of-empires-2023.view.error :as error]
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
    :add-units [add-ships/view]
    nil))

(defn game-panel []
  [:div
   [error/error-message]
   [dialog]
   [:div "Current Player" [race-selector]]
   [:div [:button {:on-click #(dispatch [::game-sync/fetch-game])} "GET GAME"]]
   [board/view]])

(defn main-panel []
  (let [logged-in? @(subscribe [::subs/login])]
    (if logged-in?
      [game-panel]
      [login/view])))

;; events

(reg-event-fx ::change-player [debug/log-event debug/validate-malli]
  (fn [fx [_ player]]
    (-> fx
        (game-sync/update-game
          #(assoc % :current-player (keyword player))))))
