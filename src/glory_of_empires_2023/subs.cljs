(ns glory-of-empires-2023.subs
  (:require
    [re-frame.core :refer [reg-sub]]
    [medley.core :refer [map-vals]]
    [glory-of-empires-2023.logic.ships :as ships]
    [glory-of-empires-2023.logic.races :as races]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.logic.utils :as utils :refer [add-vec]]))

(reg-sub ::login (fn [db _] (:login db)))

(reg-sub ::game (fn [db _] (:game db)))

(reg-sub ::board :<- [::game] (fn [{:keys [board]} _] board))

(reg-sub ::units :<- [::game] (fn [{:keys [units]} _] units))

(defn amend-unit [{:keys [type owner] :as unit}]
  (let [type-info (get ships/all-unit-types type)
        race-info (get races/all-races owner)]
    (-> unit
      (merge (select-keys type-info
               [:image-name, :image-size, :speed
                :fire-count 1, :fire-percent 20]))
      (assoc
        :category (:type type-info) ;; :ship / :ground
        :type-name (:name type-info)
        :max-hit-points (:hit-points type-info)
        :color (:unit-color race-info)
        :owner-name (:name race-info)))))

(reg-sub ::amended-units :<- [::units]
  (fn [units _] (map-vals amend-unit units)))

(reg-sub ::amended-unit :<- [::amended-units]
  (fn [units [_ id]] (get units id)))

(reg-sub ::units-by-location :<- [::amended-units]
  (fn [units _]
    (->> units
      (utils/vals-with-id)
      (group-by :location))))

(defn amend-tile [{:keys [id logical-pos system] :as tile} all-units]
  (let [units (get all-units id)]
    (-> tile
      (assoc
        :screen-pos (tiles/screen-loc logical-pos)
        :units units
        :owner (:owner (first units))) ;; TODO: Also take flag-tokens into account if no units
      (merge (-> system (tiles/all-systems) (dissoc :id))))))

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

(reg-sub ::selected-tile
  (fn [db _] (:selected-tile db))) ;; eg. :a3

(reg-sub ::selected-tile-owner :<- [::selected-tile] :<- [::units-by-location]
  (fn [[tile units-by-loc] _]
    (-> (get units-by-loc tile) (first) (:owner))))

(reg-sub ::tile-click-pos
  (fn [db _] (:tile-click-pos db))) ;; eg. [24, 167]

(reg-sub ::selected-tile? :<- [::selected-tile]
  (fn [selected-tile [_ tile-id]] (= tile-id selected-tile))) ;; eg. :a3

(reg-sub ::dialog (fn [db _] (:dialog db)))

(reg-sub ::players :<- [::game] (fn [{:keys [players]} _] players))

(reg-sub ::players-amended :<- [::players]
  (fn [players _]
    (->> players
      (map (fn [[id player]]
             [id (-> player
                   (assoc :id id)
                   (merge (get races/all-races id)))]))
      (into {}))))

(reg-sub ::current-player :<- [::game]
  (fn [game _] (:current-player game)))
