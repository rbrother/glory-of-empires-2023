(ns glory-of-empires-2023.view.ship
  (:require
    [clojure.string :as str]
    [glory-of-empires-2023.debug :as debug]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.view.ship-menu :as ship-menu]
    [glory-of-empires-2023.view.components :refer [image-dir handler-no-propagate]]))

;; views

(defn unit [{unit-name :name, :keys [id image-name image-size color offset] :as unit}]
  (let [this-selected? @(subscribe [::selected-unit? id])
        [x y] (-> (mul-vec tiles/tile-size 0.5)
                (sub-vec (mul-vec image-size 0.5))
                (add-vec offset))]
    [:div {:style {:position :absolute, :left x, :top y
                   :z-index (if this-selected? 11 10)}}
     (when this-selected? [ship-menu/view id])
     [:div
      (when this-selected?
        [:img.unit {:src (str image-dir "Ships/White/Unit-White-" image-name ".png")
                    :style {:z-index -1, :filter "brightness(4) blur(8px)", :position "absolute"}}])
      [:img.unit {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")
                  :on-click (handler-no-propagate [::click-unit id])
                  :on-drag-start #(do
                                    (set! (-> % .-dataTransfer .-effectAllowed) "move")
                                    (dispatch [::drag-unit id]))
                  :on-drag-end #(dispatch [::drag-unit-end id])}]]
     [:div.unit-id (str/upper-case (name id))
      (when unit-name (str " \"" unit-name "\""))
      ]]))

(defn view [units]
  [:<>
   (for [u units]
     ^{:key (:id u)} [unit u])])

;; subs

(reg-sub ::drag-unit (fn [db _] (:drag-unit db)))

(reg-sub ::dragging-unit? :<- [::drag-unit]
  (fn [drag-unit [_ id]] (= drag-unit id)))

(reg-sub ::selected-unit (fn [db _] (:selected-unit db)))

(reg-sub ::selected-unit? :<- [::selected-unit]
  (fn [selected-unit [_ id]] (= selected-unit id)))


;; event

(reg-event-db ::drag-unit [debug/log-event]
  (fn [db [_ id]]
    (-> db
      (assoc :drag-unit id)
      (dissoc :selected-unit
        :selected-tile))))

(reg-event-db ::drag-unit-end [debug/log-event]
  (fn [db [_ id]]
    (dissoc db :drag-unit)))

(reg-event-db ::click-unit [debug/log-event]
  (fn [{:keys [selected-unit] :as db} [_ id]]
    (if (= selected-unit id)
      (dissoc db :selected-unit)
      (-> db (assoc :selected-unit id)
        (dissoc :selected-tile)))))