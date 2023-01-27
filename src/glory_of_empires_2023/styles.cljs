(ns glory-of-empires-2023.styles
  (:require-macros [garden.def :refer [defcssfn defkeyframes]])
  (:require [spade.core :refer [defglobal]]
            [garden.units :as units]
            [garden.stylesheet :refer [at-media]]))

(defglobal
  defaults
  [:body {:color :white, :background-color :black}]
  [:.relative {:position "relative"}]
  [:.absolute {:position "absolute"}]
  [:div.board {:position "relative"}]
  [:div.tile {:position "absolute"}]
  [:div.highlight {:position "absolute", :z-index -1,
                   :transform "scale(1.03)", :visibility "hidden"}]
  [:div.tile:hover+div.highlight {:visibility "visible"}]
  [:img.tile {:z-index 1}]
  [:div.tile-id {:position :absolute, :left "112px", :top "30px", :z-index 2
                 :font-family "Arial, Helvetica, sans-serif"
                 :font-size "30px"
                 :text-shadow "2px 2px 4px rgba(0, 0, 0, 1)"}]
  [:div.unit-id {:font-family "Arial, Helvetica, sans-serif"
                 :text-align "center"
                 :font-size "20px"
                 :text-shadow "2px 2px 4px rgba(0, 0, 0, 1)"}]
  [:img.unit:hover {:filter "brightness(1.2)"}]
  )

