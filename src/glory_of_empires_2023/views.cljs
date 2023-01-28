(ns glory-of-empires-2023.views
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.components :refer [image-dir]]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.choose-system :refer [system-choice-dialog]]
    [glory-of-empires-2023.subs :as subs]))

(defn unit [{:keys [id image-name image-size color offset]}]
  (let [[x y] (-> (mul-vec tiles/tile-size 0.5)
                (sub-vec (mul-vec image-size 0.5))
                (add-vec offset))]
    [:div {:style {:position :absolute, :left x, :top y}}
     [:div [:img.unit {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")}]]
     [:div.unit-id (str/upper-case (name id))]]))

(defn tile-menu [tile-id]
  [:div.menu
   [:div.menu-title "Tile " (str/upper-case (name tile-id))]
   [:div.menu-item {:on-click #(dispatch [::choose-system])}
    "Choose System..."]])

(defn tile [{[x y] :screen-pos :keys [image id units]}]
  (let [id-str (str/upper-case (name id))
        selected? (= id @(subscribe [::subs/selected-tile]))]
    [:div.absolute {:style {:left x, :top y}}
     [:div.tile-menu-wrap {:style {:visibility (if selected? "visible" "hidden")
                                   }}
      [tile-menu id]]
     [:div.tile [:img.tile {:src (str image-dir "Tiles/" image)
                            :style (when selected? {:filter "brightness(1.25)"})
                            :on-click #(dispatch [::tile-click id])}]]
     [:div.highlight [:img {:src (str image-dir "Tiles/Setup/map-background.png")}]]
     [:div.tile-id id-str]
     (for [unit-data units]
       ^{:key (:id unit-data)} [unit unit-data])]))

(defn board []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:div.board {:style {:transform "scale(1.0)"}} ;; allow changing scale
     (for [tile-data board-data]
       ^{:key (:id tile-data)} [tile tile-data])]))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [system-choice-dialog]
    nil))

(defn main-panel []
  [:div
   [dialog]
   [board]])

;; events

(reg-event-db ::tile-click [debug/log-event]
  (fn [{earlier-tile :selected-tile :as db} [_ tile-id]]
    (assoc db :selected-tile
      (if (not= earlier-tile tile-id)
        tile-id
        nil))))

(reg-event-db ::choose-system [debug/log-event]
  (fn [db _]
    (assoc db :dialog :choose-system)))