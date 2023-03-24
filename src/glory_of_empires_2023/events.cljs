(ns glory-of-empires-2023.events
  (:require
    [re-frame.core :refer [reg-event-db]]
    [glory-of-empires-2023.debug :as debug]
    [glory-of-empires-2023.logic.map :as board]))

(reg-event-db ::initialize-db [debug/log-event debug/validate-malli]
  (fn [_ _]
    {:game {:id "Battle of Titans"
            :board (board/round-board 3)
            :players {:mentak {}
                      :yssaril {}}
            :current-player :mentak
            :units {:dr1 {:type :dr
                          :owner :mentak,
                          :location :b3
                          :offset [-50 30]
                          :name "Bismark"}
                    :fi1 {:type :fi
                          :owner :mentak,
                          :location :b3
                          :offset [0 0]}
                    :fi2 {:type :fi
                          :owner :mentak,
                          :location :b3
                          :offset [60 0]}
                    :cr2 {:type :cr
                          :owner :yssaril
                          :location :a3
                          :offset [0 0]}}}}))
