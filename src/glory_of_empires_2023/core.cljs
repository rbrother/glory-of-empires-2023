(ns glory-of-empires-2023.core
  (:require
    [glory-of-empires-2023.debug :as debug]
    [reagent.dom :as rdom]
    [re-frame.core :as re-frame :refer [reg-event-db]]
    [re-frame.db]
    [reagent-dev-tools.core :as dev-tools]
    [glory-of-empires-2023.styles] ;; Needed for global styles
    [glory-of-empires-2023.aws.cognito :as cognito]
    [glory-of-empires-2023.view.main :as views]
    [glory-of-empires-2023.config :as config]))

(defn dev-setup []
  (when config/debug?
    (do
      (println "dev mode")
      (dev-tools/start! {:state-atom re-frame.db/app-db}))))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::initialize-db])
  (mount-root)
  (dev-setup)
  (re-frame/dispatch [::cognito/login]))

(reg-event-db ::initialize-db [debug/log-event debug/validate-malli]
  (fn [_ _] {}))
