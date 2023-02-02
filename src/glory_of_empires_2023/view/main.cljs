(ns glory-of-empires-2023.view.main
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.choose-system :as choose-system]
    [glory-of-empires-2023.view.add-ships :as add-ships]
    [glory-of-empires-2023.subs :as subs]))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [choose-system/view]
    :add-ships [add-ships/view]
    nil))

(defn main-panel []
  [:div
   [dialog]
   [board/view]])

