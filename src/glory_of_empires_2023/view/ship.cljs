(ns glory-of-empires-2023.view.ship
  (:require
    [clojure.string :as str]
    [glory-of-empires-2023.debug :as debug]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                           reg-sub inject-cofx]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec distance]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.view.components :refer [image-dir]]))

;; views

(defn unit [{:keys [id image-name image-size color offset]}]
  (let [[x y] (-> (mul-vec tiles/tile-size 0.5)
                (sub-vec (mul-vec image-size 0.5))
                (add-vec offset))]
    [:div {:style {:position :absolute, :left x, :top y, :z-index 10}}
     [:div [:img.unit {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")
                       :on-drag-start #(do
                                         (set! (-> % .-dataTransfer .-effectAllowed) "move")
                                         (dispatch [::drag-unit id]))
                       :on-drag-end #(dispatch [::drag-unit-end id])}]]
     [:div.unit-id (str/upper-case (name id))]]))

(defn view [units]
  [:<>
   (for [u units]
     ^{:key (:id u)} [unit u])])

;; subs

(reg-sub ::drag-unit (fn [db _] (:drag-unit db)))

;; event

(reg-event-db ::drag-unit [debug/log-event]
  (fn [db [_ id]]
    (assoc db :drag-unit id)))

(reg-event-db ::drag-unit-end [debug/log-event]
  (fn [db [_ id]]
    (dissoc db :drag-unit)))
