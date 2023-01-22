(ns glory-of-empires-2023.core
  (:require
    [reagent.dom :as rdom]
    [re-frame.core :as re-frame]
    [glory-of-empires-2023.styles] ;; Needed for global styles
    [glory-of-empires-2023.events :as events]
    [glory-of-empires-2023.views :as views]
    [glory-of-empires-2023.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
