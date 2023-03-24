(ns glory-of-empires-2023.logic.json
  (:require [medley.core :refer [map-vals]]))

(defn walk-atomic-values [f data]
  (let [sub-walker (fn [child] (walk-atomic-values f child))]
    (cond
      (map? data) (map-vals sub-walker data)
      (set? data) (set (map sub-walker data))
      (sequential? data) (mapv sub-walker data)
      :else (f data))))

(defn- keyword-to-json [k]
  (if (keyword? k) (str "~" k)
    k))

(defn keywords-to-json [data]
  (walk-atomic-values keyword-to-json data))

(defn- keyword-from-json [s]
  (if (and (string? s) (> (count s) 2) (= (subs s 0 2) "~:"))
    (keyword (subs s 2 (count s)))
    s))

(defn keywords-from-json [data]
  (walk-atomic-values keyword-from-json data))
