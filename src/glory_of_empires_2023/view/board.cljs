(ns glory-of-empires-2023.view.board
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [vimsical.re-frame.cofx.inject :as inject]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance
                                               round-to-chunk]]
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

(defn drag-drop-ok? [{:keys [center-pos]} board-pos]
  (< (distance (sub-vec board-pos center-pos)) 160.0))

(defn handle-on-drag-over [event tile]
  (let [board-pos (event-board-pos event)]
    (when (drag-drop-ok? tile board-pos)
      (.preventDefault event))))

(defn tile [{[x y] :screen-pos :keys [image id units] :as tile}]
  (let [id-str (str/upper-case (name id))
        selected? (= id @(subscribe [::subs/selected-tile]))
        hover-on? (= id @(subscribe [::closest-tile-to-cursor]))]
    [:div.absolute {:style {:left x, :top y}}
     (when selected? [:div.tile-menu-wrap [tile-menu id]])
     [:div.tile [:img.tile {:src (str image-dir "Tiles/" image)
                            :style (when selected? {:filter "brightness(1.5)"})
                            ;; preventDefault in :on-drag-enter and :on-drag-over
                            ;; identifies the tile as drop target for the browser
                            :on-drag-enter #(do (.preventDefault %)
                                              (.log js/console "drag-enter" id))
                            :on-drag-leave #(.log js/console "drag-leave  " id)
                            :on-drag-over #(handle-on-drag-over % tile)
                            :on-drop #(dispatch [::drop-on-tile tile (event-board-pos %)])}]]
     (when hover-on? [:div.highlight tile-highlight])
     [:div.tile-id id-str]
     [ship/view units]]))

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
       [_ {tile-id :id tile-center :center-pos} drop-pos]]
    (if-not drag-unit
      db
      (let [relative-pos (sub-vec drop-pos tile-center)]
        (-> db
          (update-in [:units drag-unit]
            #(assoc % :location tile-id
               :offset (mapv round-to-chunk relative-pos))))))))

(reg-event-db ::choose-system [debug/log-event]
  (fn [db _]
    (assoc db :dialog :choose-system)))