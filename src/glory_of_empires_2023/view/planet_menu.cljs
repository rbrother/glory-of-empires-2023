(ns glory-of-empires-2023.view.planet-menu
  (:require [clojure.string :as str]
            [medley.core :refer [filter-vals remove-vals dissoc-in]]
            [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                                   reg-sub inject-cofx]]
            [glory-of-empires-2023.logic.utils :refer [attr=]]
            [glory-of-empires-2023.debug :as debug]
            [glory-of-empires-2023.subs :as subs]
            [glory-of-empires-2023.view.components :as comp]))

(defn view [planet-id]
  (let [[x y] @(subscribe [::subs/tile-click-pos])
        planet-owner @(subscribe [::subs/selected-tile-owner])
        players @(subscribe [::subs/players])]
    [:div.tile-menu-wrap
     {:style {:left (+ x 50), :top (- y 20)}}
     [:div.menu
      [:div.menu-title "Planet " (str/upper-case (name planet-id))]
      [comp/menu-item "Add units..." [::add-units (or planet-owner (first (keys players)))]]]]))

;; events

(reg-event-db ::add-units [debug/log-event debug/validate-malli]
  (fn [db [_ player]]
    (assoc db :dialog :add-units
              :add-ships {:player player})))

