(ns glory-of-empires-2023.logic.map
  (:require [medley.core :refer [index-by]]
            [glory-of-empires-2023.logic.utils :as utils]
            [glory-of-empires-2023.logic.tiles :as systems]))

(def good-letters ["a", "b", "c", "d", "e", "f", "g",
                   "h", "j", "k", "m", "n", "p",
                   "r", "s", "t", "u", "v", "z", "y", "z"])

(defn location-id [[x y] [min-x min-y]]
  (keyword (str (good-letters (- x min-x)) (+ 1 (- y min-y)))))

(defn- logical-distance [[logical-x logical-y]]
  (let [abs-x (Math/abs logical-x)
        abs-y (Math/abs logical-y)]
    (if (pos? (* logical-x logical-y)) (+ abs-x abs-y) (max abs-x abs-y))))

(def setup-tiles [:setup-red :setup-yellow :setup-light-blue :setup-medium-blue :setup-dark-blue])

(defn- setup-system [pos tile-index]
  {:logical-pos pos
   :system (nth setup-tiles (mod tile-index 5))})

(defn amend-tile-ids [map-pieces]
  (let [min-loc (utils/min-pos (map :logical-pos map-pieces))]
    (->> map-pieces
         (map (fn [tile]
                (assoc tile :id
                            (location-id (:logical-pos tile) min-loc)))))))

(defn- make-board [initial-range-size piece-filter]
  (let [a-range (range (- initial-range-size) (inc initial-range-size))]
    (->> (utils/range2d a-range a-range)
         (filter piece-filter)
         (map (fn [pos] (setup-system pos (logical-distance pos))))
         (amend-tile-ids)
         (index-by :id))))

(defn round-board [rings]
  (make-board rings (fn [pos] (< (logical-distance pos) rings))))

(defn rect-board [width height]
  (let [[tile-width tile-height] systems/tile-size
        pixel-size [(* width tile-width 0.75) (* height tile-height)]
        bounding-rect [(utils/mul-vec pixel-size -0.5) (utils/mul-vec pixel-size 0.5)]]
    (make-board (+ width height) (fn [pos] (utils/inside-rect? (systems/screen-loc pos) bounding-rect)))))
