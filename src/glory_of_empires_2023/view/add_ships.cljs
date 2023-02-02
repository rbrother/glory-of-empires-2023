(ns glory-of-empires-2023.view.add-ships
  (:require
    [clojure.string :as str]
    [glory-of-empires-2023.logic.races :as races]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-sub]]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.ships :as ships]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.view.components :refer [image-dir handler-no-propagate] :as components]))

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
                      :keys [id image-name hit-points prod-cost prod-slots
                             fire-count fire-percent speed carry special]}
                     owner]
  (let [race-info (get races/all-races owner)
        color (:unit-color race-info)
        rotate? (> height width)
        [width-rot, height-rot] (if rotate? [height width] [width height])]
    [:<>
     [:div.ship-type-grid-item (str/upper-case (name id))]
     [:div.ship-type-grid-item ship-name]
     [:div.ship-type-grid-item
      {:style {:height height-rot, :position "relative"}}
      [:img {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")
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

(defn view []
  (let [selected-tile @(subscribe [::subs/selected-tile])
        owner @(subscribe [::subs/selected-tile-owner])
        ship-types (filter #(= (:type %) :ship) ships/all-unit-types-arr)]
    [components/dialog {:title [:span (str/capitalize (name owner))
                                ": Add New Ships to system "
                                (str/upper-case (name selected-tile))]}
     [:div.ship-types-grid
      [headers1]
      [headers2]
      (for [ship-type ship-types]
        ^{:key (:id ship-type)}
        [ship-info-row ship-type owner])]
     [components/ok-cancel [::ok owner] [::cancel]]]))

;; subs

(reg-sub ::prod-counts (fn [db _] (:prod-counts db)))

(reg-sub ::prod-count :<- [::prod-counts]
  (fn [prod-counts [_ ship-type]] (get prod-counts ship-type)))

;; events

(reg-event-db ::inc-prod-count [debug/log-event]
  (fn [db [_ ship-type]] (update-in db [:prod-counts ship-type] inc)))

(reg-event-db ::dec-prod-count [debug/log-event]
  (fn [db [_ ship-type]] (update-in db [:prod-counts ship-type] dec-count)))

(reg-event-db ::ok [debug/log-event]
  (fn [{:keys [prod-counts selected-tile] :as db} [_ owner]]
    (-> db
      (update :units #(ships/create-ships % prod-counts selected-tile owner))
      (dissoc :dialog :prod-counts :selected-tile))))

(reg-event-db ::cancel [debug/log-event]
  (fn [db _] (dissoc db :dialog :prod-counts :selected-tile)))