(ns glory-of-empires-2023.logic.utils)

(defn range2d
  #_{:test (fn [] (test/is (= 9 (count (range2d (range 3) (range 3))))))}
  [range1 range2]
  (let [combine (fn [value1] (map #(vector value1 %) range2))]
    (mapcat combine range1)))

(defn round-to-chunk [num]
  (let [chunk 16]
    (* chunk (Math/round (/ num chunk)))))

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
  [[min-corner max-corner]] (map - max-corner min-corner))

(defn vals-with-id [m]
  (->> m
    (map (fn [[id attr]] (assoc attr :id id)))))