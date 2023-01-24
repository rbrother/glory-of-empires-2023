(ns glory-of-empires-2023.views
  (:require
    [re-frame.core :refer [subscribe]]
    [glory-of-empires-2023.subs :as subs]))

(defn tile [{[x y] :screen-pos img-src :img-src}]

  [:div {:style {:position "absolute", :left x, :top y}}
   [:img {:src img-src}]])

(defn board []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:div.board {:style {:transform "scale(0.75)"}} ;; allow changing scale
     (for [tile-data board-data]
       ^{:key (:id tile-data)} [tile tile-data])]))

(defn main-panel []
  [:div
   [board]])
