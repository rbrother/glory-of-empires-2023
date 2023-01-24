(ns glory-of-empires-2023.views
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe]]
    [glory-of-empires-2023.subs :as subs]))

(def image-dir "https://rjb-share.s3.eu-north-1.amazonaws.com/glory-of-empires-pics/")

(defn tile [{[x y] :screen-pos, image :image, id :id}]
  (let [id-str (str/upper-case (name id))]
    [:div {:style {:position :absolute, :left x, :top y}}
     [:img {:src (str image-dir "Tiles/" image)}]
     [:div.tile-id id-str]]))

(defn board []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:div.board {:style {:transform "scale(0.75)"}} ;; allow changing scale
     (for [tile-data board-data]
       ^{:key (:id tile-data)} [tile tile-data])]))

(defn main-panel []
  [:div
   [board]])
