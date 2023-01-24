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
  [:div.board {:position "relative" :background "red"}]
  )

