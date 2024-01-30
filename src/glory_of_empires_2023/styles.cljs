(ns glory-of-empires-2023.styles
  (:require-macros [garden.def :refer [defcssfn defkeyframes]])
  (:require [spade.core :refer [defglobal]]
            [garden.units :as units]
            [garden.stylesheet :refer [at-media]]))

(def medium-font "20px")
(def large-font "30px")

(def default-font
  {:color :white, :background-color :black
   :font-family "Arial, Helvetica, sans-serif"
   :font-size medium-font})

(defglobal
  defaults
  [:body (assoc default-font :user-select "none")]
  [:input (assoc default-font :border "1px solid white" :border-radius "3px"
                              :padding "4px" :margin "4px")]
  [:button (assoc default-font :border "1px solid white" :border-radius "3px"
                               :padding "4px 8px 4px 8px" :margin "4px" :min-width "32px")]
  [:button:hover {:filter "brightness(1.5)"}]
  [:button.ok {:margin "8px" :width "150px", :background "#050"}]
  [:button.cancel {:margin "8px" :width "150px", :background "#500"}]
  [:select (assoc default-font :border "1px solid white" :border-radius "3px"
                               :padding "4px" :margin "4px")]
  [:.relative {:position "relative"}]
  [:.absolute {:position "absolute"}]
  [:.pad {:padding "8px"}]
  [:.margin {:margin "8px"}]
  [:.bold {:font-weight "bold"}]
  [:div.flex {:display "flex" :align-items "center"}]
  [:div.error {:display "grid" :grid-template-columns "1fr auto"
               :background "#f33" :color "black" :font-weight "bold"
               :padding "8px" :align-items "center"}]
  [:div.dialog-screen {:width "100%", :height "100vh", :z-index 100
                       :background-color "rgba(0,0,0,.5)"
                       :position "fixed"}]
  [:div.dialog {:margin "100px" :height "80vh"
                :border "8px solid gray"
                :padding "8px" :background "black"
                :display "grid" :grid-template-rows "auto 1fr"
                :grid-row-gap "8px"}]
  [:div.dialog-title {:text-align "center", :font-size large-font}]
  [:div.dialog-content-wrap {:overflow "auto" :height "100%"}]
  [:div.board {:position "relative"}]
  [:div.menu {:color "black" :background "white"
              :border "2px solid black"}]
  [:div.menu-item {:padding "8px" :white-space "nowrap"
                   :font-weight "bold", :cursor "pointer"
                   :border "2px solid #dddddd"}]
  [:div.menu-title {:color "white", :background "#333333",
                    :padding "8px", :display "flex" :align-items "center"
                    :justify-content "space-between"}]
  [:div.menu-item:hover {:background "#dddddd"}]
  [:div.tile {:position "absolute"}]
  [:div.highlight {:position "absolute", :z-index 2,
                   :width "432px", :height "376px"}]
  [:img.tile {:z-index 1}]
  [:div.tile-id {:position :absolute, :left "315px", :top "94px", :z-index 2
                 :font-size large-font
                 :text-shadow "2px 2px 4px rgba(0, 0, 0, 1)"}]
  [:div.unit-id {:text-align "center"
                 :text-shadow "2px 2px 4px rgba(0, 0, 0, 1)"}]
  [:img.unit {:cursor "move"}]
  [:img.unit:hover {:filter "brightness(1.5)"}]
  [:div.ship-drop-loc {:position :absolute, :border "1px solid #ffffff40", :z-index 3}]
  [:div.tile-menu-wrap {:position "absolute", :z-index 20}]
  [:div.systems-list {:display "flex" :flex-wrap "wrap"}]
  [:div.systems-filter {:display "flex" :align-items "center" :gap "16px"}]
  [:div.system-info-box {:margin "8px" :border "4px solid gray"
                         :padding "4px" :cursor "pointer"
                         :display "grid" :grid-template-columns "auto 200px"}]
  [:div.ship-types-grid {:display "grid",
                         :grid-template-columns "auto auto 230px auto auto auto
                         auto auto auto auto auto 1fr"
                         :justify-items "stretch" :align-items "stretch"}]
  [:div.ship-type-grid-item {:border "2px solid #666", :padding "8px"}]
  [:div.system-info-box:hover {:background "#333333"}])

