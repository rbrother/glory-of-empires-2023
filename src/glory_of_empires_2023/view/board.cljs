(ns glory-of-empires-2023.view.board
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [vimsical.re-frame.cofx.inject :as inject]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.tiles :refer [tile-width tile-height tile-center]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.view.ship :as ship]
    [glory-of-empires-2023.view.components :refer [image-dir]]))

;; helpers

(defn board-pos []
  (let [board (.getElementById js/document "board")
        rect (.getBoundingClientRect board)]
    [(.-left rect), (.-top rect)]))

(defn event-board-pos [event]
  (let [client-x (-> event .-clientX)
        client-y (-> event .-clientY)]
    (sub-vec [client-x client-y] (board-pos))))

(defn closest-tile-to [tiles pos]
  (->> tiles
    (map (fn [{:keys [center-pos] :as tile}]
           (let [diff (sub-vec pos center-pos)]
             (assoc tile :distance (distance diff)))))
    (sort-by :distance)
    (first)
    (:id)))

(defn tile-menu [tile-id]
  [:div.menu
   [:div.menu-title "Tile " (str/upper-case (name tile-id))]
   [:div.menu-item {:on-click (fn [event]
                                (dispatch [::choose-system])
                                (.stopPropagation event) ;; prevent selection of another tile
                                )}
    "Choose System..."]])

(def tile-highlight
  [:svg {:viewBox "0 0 432 376"}
   [:path
    {:style {:stroke-width "4px", :stroke "white", :fill "transparent"}
     :d "M 110 4    L 4 188    L 110 372
         L 322 372  L 428 188  L 322 4    L 110 4"}]])

(def ship-location-size 16)

(defn ship-locations [{id :id :as tile}]
  [:div.absolute
   (for [x (range 0 tile-width ship-location-size)
         y (range 0 tile-height ship-location-size)
         :let [loc-center [(+ x (* 0.5 ship-location-size))
                           (+ y (* 0.5 ship-location-size))]
               offset (sub-vec loc-center tile-center)]
         :when (< (distance offset) 160)]
     ^{:key [x y]}
     [:div.ship-loc {:style {:left x, :top y}
                     ;; preventDefault in :on-drag-enter and :on-drag-over
                     ;; identifies the tile as drop target for the browser
                     :on-drag-enter #(do (.preventDefault %)
                                       (.log js/console "drag-enter" id loc-center))
                     :on-drag-leave #(.log js/console "drag-leave" id loc-center)
                     :on-drag-over #(.preventDefault %)
                     :on-drop #(dispatch [::drop-on-tile tile offset])}])])

(defn tile [{[x y] :screen-pos :keys [image id units] :as tile}]
  (let [id-str (str/upper-case (name id))
        selected? (= id @(subscribe [::subs/selected-tile]))
        hover-on? (= id @(subscribe [::closest-tile-to-cursor]))
        dragging-ship @(subscribe [::ship/drag-unit])]
    [:div.absolute {:style {:left x, :top y}}
     (when selected? [:div.tile-menu-wrap [tile-menu id]])
     [:div.tile [:img.tile {:src (str image-dir "Tiles/" image)
                            :style (when selected? {:filter "brightness(1.5)"})}]]
     [:div.tile-id id-str]
     (when dragging-ship [ship-locations tile])
     [ship/view units]
     (when hover-on? [:div.highlight tile-highlight])]))

(defn view []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:<>
     [:h1 "Glory of Empires"]
     [:div.board {:id "board"
                  :on-mouse-move #(dispatch [::board-mouse-move (event-board-pos %)])
                  :on-click #(dispatch [::board-click])}
      (for [tile-data board-data]
        ^{:key (:id tile-data)} [tile tile-data])]]))

;; subs

(reg-sub ::board-mouse-pos (fn [db _] (:board-mouse-pos db)))

(reg-sub ::closest-tile-to-cursor
  :<- [::subs/board-amended]
  :<- [::board-mouse-pos]
  (fn [[tiles mouse-pos] _] (closest-tile-to tiles mouse-pos)))

;; events

(reg-event-db ::board-mouse-move
  (fn [db [_ pos]]
    (assoc db :board-mouse-pos pos)))

(reg-event-fx ::board-click
  ;; re-frame gives for some reason warning on using sub even when we do it
  ;; in the recommended inject-cofx way. Seems to work ok though.
  [debug/log-event, (inject-cofx ::inject/sub [::closest-tile-to-cursor])]
  (fn [{{earlier-tile :selected-tile :as db} :db,
        closest ::closest-tile-to-cursor} _]
    {:db (assoc db :selected-tile
           (if (not= earlier-tile closest)
             closest
             nil))}))

(reg-event-db ::drop-on-tile [debug/log-event]
  (fn [{:keys [drag-unit] :as db}
       [_ {tile-id :id} drop-pos]]
    (cond-> db
      drag-unit (update-in [:units drag-unit]
                  #(assoc %
                     :location tile-id
                     :offset drop-pos)))))

(reg-event-db ::choose-system [debug/log-event]
  (fn [db _]
    (assoc db :dialog :choose-system)))