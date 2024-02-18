(ns glory-of-empires-2023.logic.tile-ship-locs
  (:require [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
            [glory-of-empires-2023.logic.tiles :refer [tile-center]]))

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

(defn ground-locations [planet-id]
  ;; TODO: IMPLEMENT
  [0 0]
  )