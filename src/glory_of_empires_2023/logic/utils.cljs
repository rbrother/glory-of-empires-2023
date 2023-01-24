(ns glory-of-empires-2023.logic.utils
  (:require [clojure.string :as str]))

(defn single? [coll] (= (count coll) 1))

(defn list-contains? [arr item] (some #(= % item) arr))

; From set or map it's easy to remove one item, but it's sometimes needed
; also from list when we have use case with duplicates (like TI3 AC pack)
(defn remove-single
  ;; (test/is (= [1 6 6 5 2] (remove-single [1 6 5 6 5 2] 5)))
  [arr item]
  (let [not-item-pred (fn [i] (not= i item))]
    (concat
      (take-while not-item-pred arr)
      (rest (drop-while not-item-pred arr)))))

; Clojure has already zipmap, but that does not preserve the order of items and
; does not work if there are identical items (can't have two different keys) which is sometimes important
(defn zip
  #_{:test (fn [] (test/are [x y] (= x y)
                    [[1 :a] [2 :b] [3 :c]] (zip [1 2 3] [:a :b :c])
                    [[1 :a] [2 :b]] (zip [1 2 3] [:a :b])
                    [[1 :a] [2 :b]] (zip [1 2] [:a :b :c])))}
  [list1 list2] (map vec (partition 2 (interleave list1 list2))))

(defn range2d
  #_{:test (fn [] (test/is (= 9 (count (range2d (range 3) (range 3))))))}
  [range1 range2]
  (let [combine (fn [value1] (map #(vector value1 %) range2))]
    (mapcat combine range1)))

(defn round-any [value] (if (integer? value) value (Math/round value)))

(defn min-pos
  #_{:test (fn [] (test/is (= [7 -4] (min-pos [[7 12] [8 -4]]))))}
  [vectors] (apply mapv min vectors))

(defn max-pos
  #_{:test (fn [] (test/is (= [8 12] (max-pos [[7 12] [8 -4]]))))}
  [vectors] (apply mapv max vectors))

(defn pos> [[x1 y1] [x2 y2]] (and (> x1 x2) (> y1 y2)))
(defn pos< [[x1 y1] [x2 y2]] (and (< x1 x2) (< y1 y2)))

(defn distance [vec1] (Math/sqrt (apply + (map * vec1 vec1))))

(defn mul-vec [vec1 scalar] (mapv * vec1 (repeat scalar)))

(defn add-vec [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])

(defn sub-vec [v1 v2] (add-vec v1 (mul-vec v2 -1)))

(defn inside-rect? [pos [small-corner large-corner]]
  (and (pos> pos small-corner) (pos< pos large-corner)))

(defn rect-size
  #_{:test (fn [] (test/is (= (rect-size [[-10.0 -12.0] [14.0 7.0]]) [24.0 19.0])))}
  [[min-corner max-corner]] (map - max-corner min-corner))

(defn polar [degrees distance]
  (let [radians (/ degrees 57.2958)]
    (mul-vec [(Math/sin radians) (Math/cos radians)] distance)))

(defn starts-with
  #_{:test (fn [] (test/are [x y] (= x y)
                    true (starts-with "moikka" "moi")
                    false (starts-with "moikka" "hei")))}
  [str start] (if (> (count start) (count str)) false (= start (subs str 0 (count start)))))

(defn equal-caseless?
  #_{:test (fn [] (test/are [x y] (= x y)
                    true (equal-caseless? "fOoBar" "FOObar")
                    false (equal-caseless? "fOoBar" "FOObarz")))}
  [str1 str2] (= (str/lower-case str1) (str/lower-case str2)))
