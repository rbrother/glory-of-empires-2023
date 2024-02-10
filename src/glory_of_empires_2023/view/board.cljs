(ns glory-of-empires-2023.view.board
  (:require
    [clojure.string :as str]
    [cljs.pprint :refer [pprint]]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [vimsical.re-frame.cofx.inject :as inject]
    [medley.core :refer [map-vals]]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.view.ship :as ship]
    [glory-of-empires-2023.view.drag-targets :as drag-targets]
    [glory-of-empires-2023.view.tile-menu :as tile-menu]
    [glory-of-empires-2023.view.planet-menu :as planet-menu]
    [glory-of-empires-2023.view.components :refer [image-dir]]))

;; helpers

(def planet-default-radius 75)

(defn board-pos []
  (let [board (.getElementById js/document "board")
        rect (.getBoundingClientRect board)]
    [(.-left rect), (.-top rect)]))

(defn event-board-pos [event]
  (let [client-x (-> event .-clientX)
        client-y (-> event .-clientY)]
    (sub-vec [client-x client-y] (board-pos))))

(defn amend-clicked-planet [pos {:keys [center-pos] :as system}]
  (let [clicked-pos (sub-vec pos center-pos)]
    (assoc system :mouse-on-planet
                  (->> system
                       :planets
                       (filter (fn [[_ {:keys [loc radius] :as _planet}]]
                                 (let [dist (distance (sub-vec clicked-pos loc))]
                                   (< dist (or radius planet-default-radius)))))
                       (ffirst)))))

(defn closest-tile-to [tiles pos]
  (->> tiles
       (map (fn [{:keys [center-pos] :as tile}]
              (let [diff (sub-vec pos center-pos)]
                (assoc tile :distance (distance diff)))))
       (sort-by :distance)
       first
       (amend-clicked-planet pos)))

(def tile-highlight
  [:svg {:viewBox "0 0 432 376"}
   [:path
    {:style {:stroke-width "4px", :stroke "white", :fill "transparent"}
     :d "M 110 4    L 4 188    L 110 372
         L 322 372  L 428 188  L 322 4    L 110 4"}]])

(defn planet-view [{[x y] :loc :keys [id radius]}]
  (let [hover-on? @(subscribe [::hover-on-planet? id])
        selected-planet @(subscribe [::subs/selected-planet])
        selected? (= id selected-planet)]
    (when (or hover-on? selected?)
      [:<>
       (when selected? [planet-menu/view id])
       [:div.highlight
        [:svg {:viewBox "0 0 432 376"}
         [:circle {:stroke-width "4px", :stroke "white"
                   :fill (if selected? "rgba(255,255,255,0.25)" "transparent")
                   :cx (+ 216 x) :cy (+ 188 y) :r (or radius planet-default-radius)}]]]])))

(defn tile [{[x y] :screen-pos :keys [image id units planets] :as tile}]
  (let [id-str (str/upper-case (name id))
        selected? @(subscribe [::subs/selected-tile? id])
        selected-planet @(subscribe [::subs/selected-planet])
        system-selected? (and selected? (not selected-planet))
        hover-on? @(subscribe [::hover-on-tile? id])]
    [:div.absolute {:style {:left x, :top y}}
     (when system-selected? [tile-menu/view id])
     [:div.tile [:img.tile {:src (str image-dir "Tiles/" image)
                            :style (when system-selected? {:filter "brightness(1.5)"})}]]
     [:div.tile-id id-str]
     [ship/view units]
     (when hover-on? [drag-targets/view tile])
     (when hover-on? [:div.highlight tile-highlight])
     [:<> (for [[id planet] planets]
            ^{:key id} [planet-view (assoc planet :id id)])]]))

(defn view []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:div.board {:id "board"
                 :on-mouse-move #(dispatch [::board-mouse-move (event-board-pos %)])
                 ;; mouse-move event are not generated during drag operation,
                 ;; but drag-over are generated in similar way. We need that to
                 ;; keep our custom hover-detection updated
                 :on-drag-over #(dispatch [::board-mouse-move (event-board-pos %)])
                 :on-click #(dispatch [::board-click])}
     (for [tile-data board-data]
       ^{:key (:id tile-data)} [tile tile-data])]))

;; subs

(reg-sub ::board-mouse-pos (fn [db _] (:board-mouse-pos db)))

(reg-sub ::closest-tile-to-cursor
  :<- [::subs/board-amended]
  :<- [::board-mouse-pos]
  (fn [[tiles mouse-pos] _] (closest-tile-to tiles mouse-pos)))

(reg-sub ::hover-on-tile? :<- [::closest-tile-to-cursor]
  (fn [closest-tile [_ tile-id]]
    (= (:id closest-tile) tile-id)))

(reg-sub ::hover-on-planet? :<- [::closest-tile-to-cursor]
  (fn [closest-tile [_ planet-id]]
    (= (:mouse-on-planet closest-tile) planet-id)))

;; events

(reg-event-db ::board-mouse-move
  (fn [db [_ pos]]
    (assoc db :board-mouse-pos pos)))

(reg-event-fx ::board-click
  ;; re-frame gives for some reason warning on using sub even when we do it
  ;; in the recommended inject-cofx way. Seems to work ok though.
  [debug/log-event, debug/validate-malli, (inject-cofx ::inject/sub [::closest-tile-to-cursor])]
  (fn [{{earlier-tile :selected-tile, earlier-planet :selected-planet
         :keys [board-mouse-pos] :as db} :db,
        {:keys [mouse-on-planet] :as closest} ::closest-tile-to-cursor} _]
    (let [already-selected? (= [earlier-tile earlier-planet]
                               [(:id closest) mouse-on-planet])]
      {:db (cond-> db
                   already-selected? (dissoc :selected-tile :selected-planet)
                   (not already-selected?) (assoc :selected-tile (:id closest)
                                                  :selected-planet mouse-on-planet)
                   true (assoc :tile-click-pos (sub-vec board-mouse-pos (:screen-pos closest)))
                   true (dissoc :selected-unit))})))
