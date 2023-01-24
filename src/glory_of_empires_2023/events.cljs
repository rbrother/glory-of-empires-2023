(ns glory-of-empires-2023.events
  (:require
    [re-frame.core :refer [reg-event-db]]
    [glory-of-empires-2023.logic.map :as board]))

(reg-event-db ::initialize-db
  (fn [_ _] {:board (board/round-board 3)}))
