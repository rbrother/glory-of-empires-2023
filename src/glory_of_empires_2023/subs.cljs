(ns glory-of-empires-2023.subs
  (:require
    [re-frame.core :refer [reg-sub]]
    [glory-of-empires-2023.logic.ships :as ships]
    [glory-of-empires-2023.logic.races :as races]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.logic.utils :as utils :refer [add-vec]]))

(reg-sub ::board (fn [db _] (:board db)))

(reg-sub ::units (fn [db _] (:units db)))

(defn amend-unit [{:keys [type owner] :as unit}]
  (let [{:keys [image-name image-size] :as type-info} (get ships/all-unit-types type)
        race-info (get races/all-races owner)]
    (-> unit (assoc
               :category (:type type-info) ;; :ship / :ground
               :type-name (:name type-info)
               :image-name image-name
               :image-size image-size
               :color (:unit-color race-info)
               :owner-name (:name race-info)))))

(reg-sub ::units-by-location :<- [::units]
  (fn [units _]
    (->> units
      (utils/vals-with-id)
      (map amend-unit)
      (group-by :location))))

(defn amend-tile [{:keys [id logical-pos system] :as tile} units]
  (-> tile
    (assoc :screen-pos (tiles/screen-loc logical-pos)
      :units (get units id))
    (merge (dissoc (tiles/all-systems system) :id))))

(defn shift-tiles-zero [tiles]
  (let [min (utils/min-pos (map :screen-pos tiles))]
    (->> tiles
      (mapv (fn [tile]
              (update tile :screen-pos #(utils/sub-vec % min)))))))

(defn amend-center-pos [{:keys [screen-pos] :as tile}]
  (assoc tile :center-pos (add-vec screen-pos tiles/tile-center)))

(reg-sub ::board-amended :<- [::board] :<- [::units-by-location]
  (fn [[board units] _]
    (->> board
      (vals)
      (sort-by :id)
      (map #(amend-tile % units))
      (shift-tiles-zero)
      (map amend-center-pos))))

(reg-sub ::selected-tile (fn [db _] (:selected-tile db))) ;; eg. :a3

(reg-sub ::dialog (fn [db _] (:dialog db)))