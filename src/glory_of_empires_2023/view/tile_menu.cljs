(ns glory-of-empires-2023.view.tile-menu
  (:require [clojure.string :as str]
            [medley.core :refer [filter-vals remove-vals]]
            [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                                   reg-sub inject-cofx]]
            [glory-of-empires-2023.logic.utils :refer [attr=]]
            [glory-of-empires-2023.logic.ships :refer [arrange-ships-to-tile]]
            [glory-of-empires-2023.debug :as debug]
            [glory-of-empires-2023.subs :as subs]
            [glory-of-empires-2023.view.components :as comp]))

(defn view [tile-id]
  (let [[x y] @(subscribe [::subs/tile-click-pos])
        tile-owner @(subscribe [::subs/selected-tile-owner])
        players @(subscribe [::subs/players])]
    [:div.tile-menu-wrap
     {:style {:left (+ x 20), :top (- y 20)}}
     [:div.menu
      [:div.menu-title "Tile " (str/upper-case (name tile-id))]
      [comp/menu-item "Choose System..." [::choose-system]]
      [comp/menu-item "Arrange Ships" [::arrange-ships]]
      [comp/menu-item "Add ships..." [::add-ships (or tile-owner (first (keys players)))]]]]))

;; events

(reg-event-db ::choose-system [debug/log-event debug/validate-malli]
  (fn [db _]
    (assoc db :dialog :choose-system)))

(reg-event-db ::add-ships [debug/log-event debug/validate-malli]
  (fn [db [_ player]]
    (assoc db :dialog :add-ships
      :add-ships {:player player})))

(reg-event-db ::arrange-ships [debug/log-event debug/validate-malli]
  (fn [{{:keys [board]} :game, :keys [selected-tile] :as db} _]
    (-> db
      (update-in [:game :units]
        (fn [units]
          (let [ships (->> units (vals) (filter (attr= :location selected-tile)))
                units-without-ships (->> units (remove-vals (attr= :location selected-tile)))]
            (arrange-ships-to-tile
              units-without-ships
              (get board selected-tile)
              ships))))
      (dissoc :selected-tile))))