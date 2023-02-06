(ns glory-of-empires-2023.view.ship-menu
  (:require [clojure.string :as str]
            [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                                   reg-sub inject-cofx]]
            [glory-of-empires-2023.debug :as debug]
            [glory-of-empires-2023.subs :as subs]
            [glory-of-empires-2023.view.components :refer [image-dir] :as comp]))

(defn view [ship-id]
  (let [{ship-name :name, :keys [category type-name owner-name owner] :as unit}
        @(subscribe [::subs/amended-unit ship-id])]
    (.log js/console unit)
    [:div.tile-menu-wrap {:style {:left 100, :top -40}}
     [:div.menu {:style {:min-width "240px"}}
      [:div.menu-title
       [:span "Unit " (str/upper-case (name ship-id))]
       [:img {:src (str image-dir "Flags/Flag-LgRnd-" (str/capitalize (name owner)) ".png")
              :style {}}]]
      [:div {:style {:display "grid", :grid-template-columns "auto auto"
                     :grid-row-gap "8px" :grid-column-gap "8px"
                     :padding "8px"}}
       [:div.bold "Category"] [:div (str/capitalize (name category))]
       [:div.bold "Type"] [:div type-name]
       [:div.bold "Name"] [:div (when ship-name (str "'" ship-name "'"))]
       [:div.bold "Owner"] [:div owner-name]
       [:div.bold "Hitpoints"] [:div "x / x"]]
      [comp/menu-item "Inflict Hit" [::add-hit-ship ship-id]]
      [comp/menu-item "Delete" [::delete-ship ship-id]]]]))