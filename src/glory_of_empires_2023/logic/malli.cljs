(ns glory-of-empires-2023.logic.malli
  (:require [malli.core :as m]
            [malli.util :as mu]))

(def vector2 [:vector number?])

(def unit
  [:map {:closed true}
   [:id {:optional true} :keyword]
   [:type :keyword]
   [:owner :keyword]
   [:name {:optional true} :string]
   [:location :keyword]
   [:hits-taken {:optional true} number?]
   [:offset vector2]])

(def game
  [:map {:closed true}
   [:players [:map]]
   [:board [:map]]
   [:units [:map-of :keyword unit]]
   [:current-player :keyword]])

(def app-db
  [:map {:closed true}
   [:game {:optional true} game]
   [:login {:optional true} [:map]]
   [:fetching {:optional true} :any]
   [:board-mouse-pos {:optional true} vector2]
   [:tile-click-pos {:optional true} vector2]
   [:selected-tile {:optional true} :keyword]
   [:dialog {:optional true} :keyword]
   [:drag-unit {:optional true} :keyword]
   [:selected-unit {:optional true} :keyword]
   [:drag-target {:optional true}
    [:map
     [:tile-id :keyword]
     [:loc-center vector2]]]
   [:choose-system {:optional true}
    [:map {:closed true}
     [:type-filter {:optional true} :keyword]
     [:text-filter {:optional true} :string]]]
   [:add-ships {:optional true}
    [:map {:closed true}
     [:player :keyword]
     [:prod-counts {:optional true} [:map-of :keyword number?]]]]])

