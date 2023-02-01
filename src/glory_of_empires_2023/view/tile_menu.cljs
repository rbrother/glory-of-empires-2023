(ns glory-of-empires-2023.view.tile-menu
  (:require [clojure.string :as str]
            [glory-of-empires-2023.debug :as debug]
            [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                                   reg-sub inject-cofx]]
            [glory-of-empires-2023.view.components :as comp]))

(defn view [tile-id]
  [:div.menu
   [:div.menu-title "Tile " (str/upper-case (name tile-id))]
   [comp/menu-item "Choose System..." [::choose-system]]])

;; events

(reg-event-db ::choose-system [debug/log-event]
  (fn [db _]
    (assoc db :dialog :choose-system)))