(ns glory-of-empires-2023.view.drag-targets
  (:require
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.game-sync :as game-sync]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [medley.core :refer [assoc-some dissoc-in]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :refer [tile-center]]
    [glory-of-empires-2023.logic.tile-ship-locs :refer [space-locations ship-location-size]]
    [glory-of-empires-2023.view.ship :as ship]))

(defn view [{id :id :as tile}]
  (let [dragging-ship? @(subscribe [::ship/drag-unit])
        current-drag-loc @(subscribe [::drag-target id])]
    [:div.absolute {:style {:visibility (if dragging-ship? :visible :hidden)}}
     (for [[x y] (space-locations tile)
           :let [loc-center [(+ x (* 0.5 ship-location-size))
                             (+ y (* 0.5 ship-location-size))]
                 offset (sub-vec loc-center tile-center)]]
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
           :on-drop #(dispatch [::drop-on-tile tile offset])}]))]))

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

(reg-event-fx ::drop-on-tile [debug/log-event debug/validate-malli]
  (fn [{{:keys [drag-unit]} :db :as fx}
       [_ {tile-id :id} drop-pos]]
    (-> fx
        (game-sync/update-game
          (fn [game]
            (update-in game [:units drag-unit]
                       #(assoc % :location tile-id, :offset drop-pos))))
        (dissoc-in [:db :drag-target]))))
