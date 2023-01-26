(ns glory-of-empires-2023.logic.ships
  (:require [medley.core :refer [index-by]]))

(def all-unit-types-arr
  [{:id :fi :type :ship :name "Fighter" :individual-ids false :image-name "Fighter" :image-size [50 36]}
   {:id :de :type :ship :name "Destroyer" :individual-ids true :image-name "Destroyer" :image-size [42 56]}
   {:id :cr :type :ship :name "Cruiser" :individual-ids true :image-name "Cruiser" :image-size [55 105]}
   {:id :ca :type :ship :name "Carrier" :individual-ids true :image-name "Carrier" :image-size [50 139]}
   {:id :dr :type :ship :name "Dreadnaught" :individual-ids true :image-name "Dreadnaught" :image-size [79 159]}
   {:id :ws :type :ship :name "Warsun" :individual-ids true :image-name "Warsun" :image-size [135 113]}
   {:id :fl :type :ship :name "Flagship" :individual-ids true :image-name "Flagship" :image-size [200 71]}
   {:id :gf :type :ground :name "Ground Force" :individual-ids false :image-name "GF" :image-size [48 57]}
   {:id :st :type :ground :name "Shocktroop" :individual-ids false :image-name "ST" :image-size [48 57]}
   {:id :mu :type :ground :name "Mechanised Unit" :individual-ids false :image-name "MU" :image-size [75 36]}
   {:id :pds :type :ground :name "Planetary Defence System" :individual-ids false :image-name "PDS" :image-size [67 49]}
   {:id :sd :type :ground :name "Spacedock" :individual-ids false :image-name "Spacedock" :image-size [76 78]}])

(def special-tokens-arr
  [{:id :flag :type :special :image-name "Flag" :individual-ids true :image-size [76 43]}
   {:id :cc :type :special :image-name "CC" :individual-ids true :image-size [100 88]}])

(def all-unit-types (index-by :id all-unit-types-arr))
