(ns glory-of-empires-2023.view.drag-targets
  (:require
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.game-sync :as game-sync]
    [glory-of-empires-2023.logic.ships :as ships]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [medley.core :refer [assoc-some dissoc-in]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :refer [tile-center]]
    [glory-of-empires-2023.logic.tile-ship-locs
     :refer [space-locations ship-location-size target-loc-id]]
    [glory-of-empires-2023.view.ship :as ship]))

(defn view [{id :id :as tile}]
  (let [dragging-unit @(subscribe [::ship/drag-unit])
        unit-type (some-> dragging-unit name (subs 0 2) keyword)
        ship? (some-> unit-type (ships/all-unit-types) :type (= :ship))
        current-drag-loc @(subscribe [::drag-target id])]
    [:div.absolute {:style {:visibility (if dragging-unit :visible :hidden)}}
     (for [[x y] (space-locations tile (if ship? :space :ground))
           :let [loc-center [(+ x (* 0.5 ship-location-size))
                             (+ y (* 0.5 ship-location-size))]]]
       (let [this-current? (= loc-center current-drag-loc)]
         ^{:key loc-center}
         [:div.ship-drop-loc
          {:style (assoc-some {:left x, :top y, :width (dec ship-location-size),
                               :height (dec ship-location-size)}
                              :background (when this-current? "yellow"))
           ;; preventDefault in :on-drag-enter and :on-drag-over
           ;; identifies the tile as drop target for the browser
           :on-drag-enter #(do (.preventDefault %)
                               (dispatch [::drag-enter id loc-center]))
           :on-drag-over #(.preventDefault %)
           :on-drop #(dispatch [::drop-on-target tile loc-center])}]))]))

;; subs

(reg-sub ::drag-target
  (fn [{:keys [drag-target]} [_ tile-id]]
    (when (= (:tile-id drag-target) tile-id)
      (:loc-center drag-target))))

;; events

(reg-event-db ::drag-enter [debug/validate-malli]
  (fn [db [_ tile-id loc-center]]
    (assoc db :drag-target
              {:tile-id tile-id, :loc-center loc-center})))

(reg-event-fx ::drop-on-target [debug/log-event debug/validate-malli]
  (fn [{{:keys [drag-unit]} :db :as fx}
       [_ tile drop-pos]]
    (let [target-loc (target-loc-id drop-pos tile)]
      (-> fx
          (game-sync/update-game
            (fn [game]
              (update-in game [:units drag-unit]
                         #(assoc % :location target-loc 
                                   :offset (sub-vec drop-pos tile-center)))))
          (dissoc-in [:db :drag-target])))))
