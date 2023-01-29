(ns glory-of-empires-2023.view.main
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.view.board :as board]
    [glory-of-empires-2023.view.choose-system :refer [system-choice-dialog]]
    [glory-of-empires-2023.subs :as subs]))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [system-choice-dialog]
    nil))

(defn main-panel []
  [:div
   [dialog]
   [board/view]])

