(ns glory-of-empires-2023.view.choose-system
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-sub]]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.subs :as subs]
    [glory-of-empires-2023.view.components :as components]))

(defn system-info-box [{:keys [id image]}]
  [:div.system-info-box {:on-click (components/handler-no-propagate [::click-system id])}
   [:div [:img {:src (str components/image-dir "Tiles/" image)
                :style {:height (* 0.5 tiles/tile-height)
                        :width (* 0.5 tiles/tile-width)}}]]
   [:div (str/capitalize (name id))]])

(defn systems-filter []
  (let [type-filter @(subscribe [::type-filter])
        text-filter @(subscribe [::text-filter])]
    [:div.systems-filter
     [:div "System Type"]
     [:select {:value (if type-filter (name type-filter) "all")
               :on-change #(dispatch [::change-system-type-filter (-> % .-target .-value)])}
      [:option {:value "all"} "All Systems"]
      [:option {:value "1planet"} "1 Planet"]
      [:option {:value "2planet"} "2 Planets"]
      [:option {:value "3planet"} "3 Planets"]
      [:option {:value "home-system"} "Home Systems"]
      [:option {:value "special"} "Special Systems"]
      [:option {:value "setup"} "Setup Systems"]]
     [:div "Text filter"]
     [:input {:type "text" :id "systems-text-filter"
              :value text-filter
              :on-change #(dispatch [::change-system-text-filter (-> % .-target .-value)])}]]))

(defn view []
  (let [selected-tile @(subscribe [::subs/selected-tile])
        filtered-systems @(subscribe [::systems])]
    [components/dialog {:title [:span "Choose system for location "
                                (str/upper-case (name selected-tile))]}
     [:div {:style {:display "grid" :grid-template-rows "auto 1fr" :height "100vh"}}
      [systems-filter]
      [:div.systems-list
       (for [system filtered-systems]
         ^{:key (:id system)} [system-info-box system])]]]))

;; subs

(reg-sub ::type-filter
  (fn [db _] (get-in db [:choose-system :type-filter])))

(reg-sub ::text-filter
  (fn [db _]
    (or (get-in db [:choose-system :text-filter]) "")))

(defn filter-system-by-text [text systems]
  (let [pat (re-pattern (str "(?i)" text))]
    (->> systems
      (filter (fn [{:keys [id]}]
                (re-find pat (name id)))))))

(reg-sub ::systems :<- [::type-filter] :<- [::text-filter]
  (fn [[type-filter text-filter] _]
    (cond->> tiles/all-systems-list
      type-filter (filter (fn [{:keys [type]}]
                            (= type type-filter)))
      (not= text-filter "") (filter-system-by-text text-filter))))

;; events

(reg-event-db ::change-system-type-filter [debug/log-event debug/validate-malli]
  (fn [db [_ value]]
    (assoc-in db [:choose-system :type-filter]
      (if (= value "all") nil
        (keyword value)))))

(reg-event-db ::change-system-text-filter [debug/log-event debug/validate-malli]
  (fn [db [_ value]]
    (assoc-in db [:choose-system :text-filter] value)))

(reg-event-db ::click-system [debug/log-event debug/validate-malli]
  (fn [{tile :selected-tile :as db} [_ system-id]]
    (-> db
      (assoc-in [:game :board tile :system] system-id)
      (dissoc :dialog, :choose-system, :selected-tile))))