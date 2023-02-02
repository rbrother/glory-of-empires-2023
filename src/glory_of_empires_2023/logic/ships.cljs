(ns glory-of-empires-2023.logic.ships
  (:require [medley.core :refer [index-by]]))

(def all-unit-types-arr
  [{:id :fi :type :ship :name "Fighter" :individual-ids false
    :image-name "Fighter" :image-size [50 36]
    :hit-points 1, :fire-count 1, :fire-percent 20
    :prod-slots 1, :prod-cost 0.5, :speed 0}
   {:id :de :type :ship :name "Destroyer" :individual-ids true
    :image-name "Destroyer" :image-size [42 56]
    :hit-points 1, :fire-count 1, :fire-percent 20
    :prod-slots 1, :prod-cost 1, :speed 2
    :special "Anti-Fighter Barrage"}
   {:id :cr :type :ship :name "Cruiser" :individual-ids true
    :image-name "Cruiser" :image-size [55 105]
    :hit-points 1, :fire-count 1, :fire-percent 40
    :prod-slots 1, :prod-cost 2, :speed 2}
   {:id :ca :type :ship :name "Carrier" :individual-ids true
    :image-name "Carrier" :image-size [50 139]
    :hit-points 1, :fire-count 1, :fire-percent 20, :carry 6
    :prod-slots 1, :prod-cost 3, :speed 1}
   {:id :dr :type :ship :name "Dreadnaught" :individual-ids true
    :image-name "Dreadnaught" :image-size [79 159]
    :hit-points 2, :fire-count 2, :fire-percent 60
    :prod-slots 2, :prod-cost 5, :speed 1}
   {:id :ws :type :ship :name "Warsun" :individual-ids true
    :image-name "Warsun" :image-size [135 113]
    :hit-points 3, :fire-count 3, :fire-percent 80, :carry 6
    :prod-slots 3, :prod-cost 12, :speed 2}
   {:id :fl :type :ship :name "Flagship" :individual-ids true
    :image-name "Flagship" :image-size [200 71]
    :hit-points 2, :special "Race-specific abilities"}
   {:id :gf :type :ground :name "Ground Force" :individual-ids false
    :image-name "GF" :image-size [48 57]
    :hit-points 1, :fire-count 1, :fire-percent 30
    :prod-slots 1, :prod-cost 0.5}
   {:id :st :type :ground :name "Shocktroop" :individual-ids false
    :image-name "ST" :image-size [48 57]
    :hit-points 1, :fire-count 1, :fire-percent 60}
   {:id :mu :type :ground :name "Mechanised Unit" :individual-ids false
    :image-name "MU" :image-size [75 36]
    :hit-points 2, :fire-count 2,
    :prod-slots 2, :prod-cost 2}
   {:id :pds :type :ground :name "Planetary Defence System" :individual-ids false
    :image-name "PDS" :image-size [67 49]
    :hit-points 1, :fire-count 1, :fire-percent 60
    :prod-slots 1, :prod-cost 2}
   {:id :sd :type :ground :name "Spacedock" :individual-ids false
    :image-name "Spacedock" :image-size [76 78]
    :hit-points 1, :fire-count 0, :fighter-support 3
    :prod-slots 0, :prod-cost 4}])

(def special-tokens-arr
  [{:id :flag :type :special :image-name "Flag" :individual-ids true :image-size [76 43]}
   {:id :cc :type :special :image-name "CC" :individual-ids true :image-size [100 88]}])

(def all-unit-types (index-by :id all-unit-types-arr))

(defn free-id [existing-units ship-type]
  (->> existing-units
    (keys)
    (map (fn [id] (name id)))
    (filter (fn [id] (= ship-type (keyword (subs id 0 2)))))
    (map (fn [id] (js/parseInt (subs id 2))))
    (cons 0)
    (apply max)
    (inc)
    (str (name ship-type))
    (keyword)))

(defn create-ships [existing-units prod-counts tile owner]
  (let [prod-array (->> prod-counts
                     (mapcat (fn [[type count]] (repeat count type)))) ;; [:fi :fi :dr]
        create-ship (fn [existing-units type]
                      (let [id (free-id existing-units type)]
                        (assoc existing-units
                          id {:type type
                              :owner owner
                              :location tile
                              :offset [0 0]})))]
    (reduce create-ship existing-units prod-array)))
