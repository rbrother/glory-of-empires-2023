(ns glory-of-empires-2023.styles
  (:require-macros [garden.def :refer [defcssfn]])
  (:require [spade.core :refer [defglobal]]
            [garden.units :as units]
            [garden.stylesheet :refer [at-media]]))

(defglobal
  defaults
  [:body {:color :white
          :background-color :black}]
  [:div.relative {:position "relative"}]
  [:div.board {:position "relative"}]
  [:div.tile-id {:position :absolute, :left "112px", :top "30px", :z-order 1
                 :font-family "Arial, Helvetica, sans-serif"
                 :font-size "30px"
                 :text-shadow "2px 2px 4px rgba(0, 0, 0, 1)" }]
  )

