(ns glory-of-empires-2023.board
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [vimsical.re-frame.cofx.inject :as inject]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.components :refer [image-dir]]))

;; helpers

(defn round-to-chunk [num]
  (let [chunk 16]
    (* chunk (Math/round (/ num chunk)))))

(defn board-pos []
  (let [board (.getElementById js/document "board")
        rect (.getBoundingClientRect board)]
    [(.-left rect), (.-top rect)]))

(defn event-board-pos [event]
  (let [client-x (-> event .-clientX)
        client-y (-> event .-clientY)]
    (sub-vec [client-x client-y] (board-pos))))

;; views
(defn unit [{:keys [id image-name image-size color offset]}]
  (let [[x y] (-> (mul-vec tiles/tile-size 0.5)
                (sub-vec (mul-vec image-size 0.5))
                (add-vec offset))]
    [:div {:style {:position :absolute, :left x, :top y, :z-index 10}}
     [:div [:img.unit {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")
                       :on-drag-start #(dispatch [::drag-unit id])
                       :on-drag-end #(dispatch [::drag-unit-end id])
                       :on-click #(do (dispatch [::click-ship id])
                                    (.stopPropagation %))}]]
     [:div.unit-id (str/upper-case (name id))]]))

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
     :d "M 110 4
           L 4 188
           L 110 372
           L 322 372
           L 428 188
           L 322 4
           L 110 4"}]])

(defn tile [{[x y] :screen-pos :keys [image id units center-pos] :as tile}]
  (let [id-str (str/upper-case (name id))
        selected? (= id @(subscribe [::subs/selected-tile]))
        hover-on? (= id @(subscribe [::closest-tile-to-cursor]))]
    [:div.absolute {:style {:left x, :top y}}
     (when selected? [:div.tile-menu-wrap [tile-menu id]])
     [:div.tile [:img.tile {:src (str image-dir "Tiles/" image)
                            :style (when selected? {:filter "brightness(1.5)"})
                            ;; preventDefault in :on-drag-enter and :on-drag-over
                            ;; identifies the tile as drop target for the browser
                            :on-drag-enter #(.preventDefault %)
                            :on-drag-over #(.preventDefault %)
                            :on-drop #(dispatch [::drop-on-tile tile (event-board-pos %)])}]]
     (when hover-on? [:div.highlight tile-highlight])
     [:div.tile-id id-str]
     (for [unit-data units]
       ^{:key (:id unit-data)} [unit unit-data])]))

(defn view []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:<>
     [:h1 "Glory of Empires"]
     [:div.board {:id "board"
                  :style {:transform "scale(1.0)"} ;; allow changing scale
                  :on-mouse-move #(dispatch [::board-mouse-move (event-board-pos %)])
                  :on-click #(dispatch [::board-click])}
      (for [tile-data board-data]
        ^{:key (:id tile-data)} [tile tile-data])]]))

;; subs

(reg-sub ::board-mouse-pos (fn [db _] (:board-mouse-pos db)))

(defn closest-tile-to [tiles pos]
  (->> tiles
    (map (fn [{:keys [center-pos] :as tile}]
           (let [diff (sub-vec pos center-pos)]
             (assoc tile :distance (distance diff)))))
    (sort-by :distance)
    (first)
    (:id)))

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

(reg-event-db ::click-ship [debug/log-event]
  (fn [db [_ id]]
    db
    ))

(reg-event-db ::drag-unit [debug/log-event]
  (fn [db [_ id]]
    (assoc db :drag-unit id)))

(reg-event-db ::drag-unit-end [debug/log-event]
  (fn [db [_ id]]
    (dissoc db :drag-unit)))

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