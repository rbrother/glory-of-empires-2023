(ns glory-of-empires-2023.subs
  (:require
    [re-frame.core :refer [reg-sub]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.logic.utils :as utils]))

(reg-sub ::board (fn [db] (:board db)))

(defn amend-tile [{:keys [logical-pos system] :as tile}]
  (-> tile
    (assoc :screen-pos (tiles/screen-loc logical-pos))
    (merge (dissoc (tiles/all-systems system) :id))))

(defn shift-tiles-zero [tiles]
  (let [min (utils/min-pos (map :screen-pos tiles))]
    (->> tiles
      (mapv (fn [tile]
              (update tile :screen-pos #(utils/sub-vec % min)))))))

(reg-sub ::board-amended :<- [::board]
  (fn [board _]
    (->> board
      (vals)
      (sort-by :id)
      (map amend-tile)
      (shift-tiles-zero))))