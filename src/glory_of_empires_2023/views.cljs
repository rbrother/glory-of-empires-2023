(ns glory-of-empires-2023.views
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.board :as board]
    [glory-of-empires-2023.choose-system :refer [system-choice-dialog]]
    [glory-of-empires-2023.subs :as subs]))

(defn dialog []
  (case @(subscribe [::subs/dialog])
    :choose-system [system-choice-dialog]
    nil))

(defn main-panel []
  [:div
   [dialog]
   [board/view]])

