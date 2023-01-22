(ns glory-of-empires-2023.views
  (:require
    [re-frame.core :as re-frame]
    [glory-of-empires-2023.subs :as subs]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Glory of Empires 2023"]]))
