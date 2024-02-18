(ns glory-of-empires-2023.view.add-ships
  (:require
    [clojure.string :as str]
    [cljs.pprint :refer [pprint]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx reg-sub]]
    [glory-of-empires-2023.logic.utils :refer [attr=]]
    [glory-of-empires-2023.game-sync :as game-sync]
    [glory-of-empires-2023.logic.races :as races]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.ships :as ships]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.view.components
     :refer [image-dir handler-no-propagate] :as components]))

;; helpers

(defn dec-count [n] (max (dec n) 0))

;; views

(defn production-count-buttons [ship-type]
  (let [prod-count (or @(subscribe [::prod-count ship-type]) 0)]
    [:div.flex
     [:button {:style {:background "#050"}
               :on-click (handler-no-propagate [::inc-prod-count ship-type])} "+"]
     [:span.pad {:style (when (zero? prod-count) {:color "#777"})} prod-count]
     [:button {:style {:background "#500"}
               :on-click (handler-no-propagate [::dec-prod-count ship-type])} "-"]]))

(defn ship-info-row [{ship-name :name, [width height] :image-size,
                      :keys [id image-name hit-points prod-cost prod-slots token rotation
                             fire-count fire-percent speed carry special]}
                     owner]
  (let [race-info (get races/all-races owner)
        color (:unit-color race-info)
        rotate? (= rotation 90)
        [width-rot, height-rot] (if rotate? [height width] [width height])]
    [:<>
     [:div.ship-type-grid-item (str/upper-case (name id))]
     [:div.ship-type-grid-item ship-name]
     [:div.ship-type-grid-item
      {:style {:height height-rot, :position "relative"}}
      [:img {:src (if token
                    (str image-dir "Ships/" image-name ".png")
                    (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png"))
             :style {:position "absolute"
                     :left (+ 8 (* 0.5 (- height height-rot)))
                     :top (+ 8 (* 0.5 (- width width-rot)))
                     :transform (when rotate? "rotate(90deg)")}}]]
     [:div.ship-type-grid-item prod-cost]
     [:div.ship-type-grid-item prod-slots]
     [:div.ship-type-grid-item hit-points]
     [:div.ship-type-grid-item
      (when (> fire-count 1) (str fire-count " x "))
      (when fire-percent (str fire-percent "%"))]
     [:div.ship-type-grid-item (when (> speed 0) speed)]
     [:div.ship-type-grid-item (when carry carry)]
     [:div.ship-type-grid-item special]
     [:div.ship-type-grid-item [production-count-buttons id]]
     [:div]]))

(defn headers1 []
  [:<>
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item {:style {:grid-column-end "span 2"}} "Production"]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div.ship-type-grid-item]
   [:div]])

(defn headers2 []
  [:<>
   [:div.ship-type-grid-item.bold "ID"]
   [:div.ship-type-grid-item.bold "Name"]
   [:div.ship-type-grid-item.bold "Image"]
   [:div.ship-type-grid-item.bold "Cost"]
   [:div.ship-type-grid-item.bold "Slots"]
   [:div.ship-type-grid-item.bold "HP"]
   [:div.ship-type-grid-item.bold "Attack"]
   [:div.ship-type-grid-item.bold "Speed"]
   [:div.ship-type-grid-item.bold "Carry"]
   [:div.ship-type-grid-item.bold "Special"]
   [:div.ship-type-grid-item.bold "Produce"]
   [:div]])

(defn title []
  (let [dialog-type @(subscribe [::subs/dialog])
        selected-tile @(subscribe [::subs/selected-tile])
        selected-planet @(subscribe [::subs/selected-planet])]
    [:span (if (= dialog-type :add-units)
             (str "Add New Units to planet " (str/capitalize (name selected-planet)))
             (str "Add New Ships to system " (str/capitalize (name selected-tile))))]))

(defn ship-types []
  (let [dialog-type @(subscribe [::subs/dialog])
        unit-type (if (= dialog-type :add-ships) :ship :ground)]
    (filter (attr= :type unit-type) ships/all-unit-types-arr)))

(defn view []
  (let [player @(subscribe [::subs/current-player])]
    [components/dialog {:title [title]}
     [:div.ship-types-grid
      [headers1]
      [headers2]
      (for [ship-type (ship-types)]
        ^{:key (:id ship-type)}
        [ship-info-row ship-type player])]
     [components/ok-cancel [::ok] [::cancel]]]))

;; subs

(reg-sub ::add-ships (fn [db _] (:add-ships db)))           ;; State of the dialog

(reg-sub ::prod-counts :<- [::add-ships]
  (fn [add-ships _] (:prod-counts add-ships)))

(reg-sub ::prod-count :<- [::prod-counts]
  (fn [prod-counts [_ ship-type]] (get prod-counts ship-type)))

;; events

(reg-event-db ::inc-prod-count [debug/log-event debug/validate-malli]
  (fn [db [_ ship-type]]
    (update-in db [:add-ships :prod-counts ship-type] inc)))

(reg-event-db ::dec-prod-count [debug/log-event debug/validate-malli]
  (fn [db [_ ship-type]]
    (update-in db [:add-ships :prod-counts ship-type] dec-count)))

(reg-event-fx ::ok [debug/log-event debug/validate-malli]
  (fn [{{:keys [selected-tile selected-planet]
         {:keys [prod-counts]} :add-ships
         {:keys [board current-player]} :game} :db :as fx} _]
    (let [tile (get board selected-tile)
          planet (some-> selected-planet, tiles/all-planets
                         (assoc :id selected-planet))]
      (-> fx
          (game-sync/update-game
            (fn [game]
              (update game :units
                      #(ships/create-ships % prod-counts tile planet current-player))))
          (update :db #(dissoc % :dialog :add-ships :selected-tile))))))

(reg-event-db ::cancel [debug/log-event debug/validate-malli]
  (fn [db _] (dissoc db :dialog :add-ships :selected-tile)))
