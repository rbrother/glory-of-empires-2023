(ns glory-of-empires-2023.view.main
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.view.board :as board]
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

(defn main-panel []
  [:div
   [dialog]
   [:div "Current Player" [race-selector]]
   [board/view]])

;; events

(reg-event-db ::change-player
  (fn [db [_ player]]
    (assoc db :current-player (keyword player))))
