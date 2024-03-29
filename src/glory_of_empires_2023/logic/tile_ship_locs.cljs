(ns glory-of-empires-2023.logic.tile-ship-locs
  (:require [clojure.set :refer [difference]]
            [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
            [glory-of-empires-2023.logic.tiles :refer [tile-center]]))

(def ship-location-size 20)

(def target-center (mul-vec [ship-location-size ship-location-size] 0.5))

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
                      (map (fn [n] [(+ x (* n ship-location-size)), y])))))))

(defn pos-on-planet? [pos {planet-loc :loc radius :radius :as _planet}]
  (< (distance (sub-vec pos (add-vec planet-loc tile-center)))
     (or radius 80)))

;; Does pos represent planet or the tile itself?
(defn target-loc-id [pos {:keys [planets] :as tile}]
  (let [planet (->> planets, vals
                    (filter #(pos-on-planet? pos %))
                    first)]
    (or (:id planet) (:id tile))))

(defn planet-locations [planet]
  (->> drag-target-locs
       (filter (fn [loc]
                 (pos-on-planet? (add-vec loc target-center) planet)))))

(defn all-planet-locations [planets]
  (->> planets, vals
       (mapcat planet-locations)))

(defn space-locations [planets]
  (difference (set drag-target-locs) (set (all-planet-locations planets))))
