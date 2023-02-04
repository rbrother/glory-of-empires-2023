(ns glory-of-empires-2023.view.drag-targets
  (:require
    [glory-of-empires-2023.debug :as debug]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [medley.core :refer [assoc-some]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :refer [tile-width tile-height tile-center]]
    [glory-of-empires-2023.view.ship :as ship]))

(def ship-location-size 20)

(def drag-target-rows-half
  [{:y 30, :x 115, :count 10}
   {:y 50, :x 95, :count 12}
   {:y 70, :x 95, :count 12}
   {:y 90, :x 75, :count 14}
   {:y 110, :x 75, :count 14}
   {:y 130, :x 55, :count 16}
   {:y 150, :x 55, :count 16}
   {:y 170, :x 35, :count 18}])

(def drag-target-rows
  (concat drag-target-rows-half
    (->> drag-target-rows-half
      (map (fn [{:keys [y] :as row}]
             (assoc row :y (- 360 y)))))))

(def drag-target-locs
  (->> drag-target-rows
    (mapcat (fn [{:keys [x y count]}]
              (->> (range count)
                (map (fn [n] [(+ x (* n ship-location-size)), y])))))
    (vec)))

(defn space-location? [pos planet-locs]
  (->> planet-locs
    (some (fn [planet-loc]
            (< (distance (sub-vec pos planet-loc)) 80)))
    (not)))

(defn space-locations [tile]
  (let [planet-locs (->> tile (:planets) (vals) (map :loc)
                      (map #(add-vec % tile-center)))
        target-center (mul-vec [ship-location-size ship-location-size] 0.5)]
    (->> drag-target-locs
      (filter (fn [loc]
                (space-location? (add-vec loc target-center) planet-locs)))
      (vec))))

(defn view [{id :id :as tile}]
  (let [dragging-ship @(subscribe [::ship/drag-unit])
        current-drag-loc @(subscribe [::drag-target id])]
    [:div.absolute {:style {:visibility (if dragging-ship :visible :hidden)}}
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

(reg-event-db ::drag-enter
  (fn [db [_ tile-id loc-center]]
    (assoc db :drag-target
      {:tile-id tile-id, :loc-center loc-center})))

(reg-event-db ::drop-on-tile [debug/log-event]
  (fn [{:keys [drag-unit] :as db}
       [_ {tile-id :id} drop-pos]]
    (cond-> db
      drag-unit (update-in [:units drag-unit]
                  #(assoc %
                     :location tile-id
                     :offset drop-pos))
      true (dissoc :drag-target))))
