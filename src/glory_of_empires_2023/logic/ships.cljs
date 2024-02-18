(ns glory-of-empires-2023.logic.ships
  (:require [medley.core :refer [index-by]]
            [glory-of-empires-2023.logic.utils
             :refer [mul-vec add-vec sub-vec distance attr=]]
            [glory-of-empires-2023.logic.tiles :as tiles]
            [glory-of-empires-2023.logic.tile-ship-locs
             :refer [space-locations planet-locations]]))

(def all-unit-types-arr
  [{:id :fi :type :ship :name "Fighter" :individual-ids false
    :image-name "Fighter" :image-size [50 36]
    :hit-points 1, :fire-count 1, :fire-percent 20
    :prod-slots 1, :prod-cost 0.5, :speed 0}
   {:id :de :type :ship :name "Destroyer" :individual-ids true
    :image-name "Destroyer" :image-size [42 56], :rotation 90
    :hit-points 1, :fire-count 1, :fire-percent 20
    :prod-slots 1, :prod-cost 1, :speed 2
    :special "Anti-Fighter Barrage"}
   {:id :cr :type :ship :name "Cruiser" :individual-ids true
    :image-name "Cruiser" :image-size [55 105], :rotation 90
    :hit-points 1, :fire-count 1, :fire-percent 40
    :prod-slots 1, :prod-cost 2, :speed 2}
   {:id :ca :type :ship :name "Carrier" :individual-ids true
    :image-name "Carrier" :image-size [50 139], :rotation 90
    :hit-points 1, :fire-count 1, :fire-percent 20, :carry 6
    :prod-slots 1, :prod-cost 3, :speed 1}
   {:id :dr :type :ship :name "Dreadnaught" :individual-ids true
    :image-name "Dreadnaught" :image-size [79 159], :rotation 90
    :hit-points 2, :fire-count 2, :fire-percent 60
    :prod-slots 2, :prod-cost 5, :speed 1}
   {:id :ws :type :ship :name "Warsun" :individual-ids true
    :image-name "Warsun" :image-size [135 113]
    :hit-points 3, :fire-count 3, :fire-percent 80, :carry 6
    :prod-slots 3, :prod-cost 12, :speed 2}
   {:id :fl :type :ship :name "Flagship" :individual-ids true
    :image-name "Flagship" :image-size [200 71]
    :hit-points 2, :special "Race-specific abilities"}
   {:id :gf, :type :ground, :name "Ground Force", :individual-ids false
    :image-name "GF", :image-size [48 57]
    :hit-points 1, :fire-count 1, :fire-percent 30
    :prod-slots 1, :prod-cost 0.5}
   {:id :st :type :ground :name "Shocktroop" :individual-ids false
    :image-name "ShockTroop", :token true, :image-size [56 56]
    :hit-points 1, :fire-count 1, :fire-percent 60}
   {:id :mu :type :ground :name "Mechanised Unit" :individual-ids false ;; TI3 Shards of the Throne expansion
    :image-name "MU" :image-size [75 36], :rotation 0
    :hit-points 2, :fire-count 2, :fire-percent 50
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

(defn choose-new-ship-offset [ship-type available-locs units-in-the-tile]
  (let [unit-size (-> all-unit-types ship-type :image-size first)
        placement-value
        (fn [loc-offset]
          (->> units-in-the-tile
               (map (fn [{other-offset :offset, other-type :type}]
                      (let [dist (distance (sub-vec other-offset loc-offset))
                            same-type (= ship-type other-type)]
                        (+ (when (< dist unit-size) (/ 20000 (inc dist))) ;; avoid overlap
                           (if same-type dist (- dist)))))) ;; try to keep same together ;; separate ones keep away
               (apply +)))]
    (->> available-locs
         (map (fn [loc]
                (let [loc-offset (sub-vec loc tiles/tile-center)]
                  {:loc loc-offset,
                   :placement-value (placement-value loc-offset)})))
         (sort-by :placement-value)
         first
         :loc)))

(defn arrange-unit [all-existing-units location-id target-locs {unit-type :type :as unit}]
  (let [unit-id (or (:id unit) (free-id all-existing-units unit-type))
        units-in-the-location (->> all-existing-units, vals
                                   (filter (attr= :location location-id)))]
    (assoc all-existing-units
      unit-id (assoc unit
                :id unit-id
                :offset (choose-new-ship-offset unit-type target-locs units-in-the-location)))))

(defn arrange-units-to-locs [existing-units location-id target-locs new-units]
  (reduce #(arrange-unit %1 location-id target-locs %2)
          existing-units new-units))

(defn arrange-ships-to-tile [existing-units {tile-id :id, system :system :as tile} new-ships]
  (arrange-units-to-locs existing-units, tile-id
                         (space-locations (-> system tiles/all-systems :planets))
                         new-ships))

(defn arrange-units-to-planet [existing-units {planet-id :id :as planet} new-units]
  (arrange-units-to-locs existing-units, planet-id
                         (planet-locations planet)
                         new-units))

(defn create-ships [existing-units prod-counts {tile-id :id, :as tile}
                    {planet-id :id :as planet} owner]
  (let [new-units (->> prod-counts
                       (mapcat (fn [[type count]] (repeat count type))) ;; [:fi :fi :dr]
                       (map (fn [type]
                              {:type type
                               :owner owner
                               :location (or planet-id tile-id)
                               :hits-taken 0})))]
    (if planet
      (arrange-units-to-planet existing-units planet new-units)
      (arrange-ships-to-tile existing-units tile new-units))))

(defn inflict-hit [unit] (update unit :hits-taken inc))