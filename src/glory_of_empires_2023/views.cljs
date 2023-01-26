(ns glory-of-empires-2023.views
  (:require
    [clojure.string :as str]
    [re-frame.core :refer [subscribe]]
    [glory-of-empires-2023.logic.utils :refer [mul-vec add-vec sub-vec]]
    [glory-of-empires-2023.logic.tiles :as tiles]
    [glory-of-empires-2023.subs :as subs]))

(def image-dir "https://rjb-share.s3.eu-north-1.amazonaws.com/glory-of-empires-pics/")

(defn unit [{:keys [id image-name image-size color offset]}]
  (let [[x y] (-> (mul-vec tiles/tile-size 0.5)
                (sub-vec (mul-vec image-size 0.5))
                (add-vec offset))]
    [:div {:style {:position :absolute, :left x, :top y}}
     [:div [:img {:src (str image-dir "Ships/" color "/Unit-" color "-" image-name ".png")}]]
     [:div.unit-id (str/upper-case (name id))]]))

(defn tile [{[x y] :screen-pos :keys [image id units]}]
  (let [id-str (str/upper-case (name id))]
    [:div {:style {:position :absolute, :left x, :top y}}
     [:img {:src (str image-dir "Tiles/" image)}]
     [:div.tile-id id-str]
     (for [unit-data units]
       ^{:key (:id unit-data)} [unit unit-data])]))

(defn board []
  (let [board-data @(subscribe [::subs/board-amended])]
    [:div.board {:style {:transform "scale(0.75)"}} ;; allow changing scale
     (for [tile-data board-data]
       ^{:key (:id tile-data)} [tile tile-data])]))

(defn main-panel []
  [:div
   [board]])
