(ns glory-of-empires-2023.logic.game
  (:require [glory-of-empires-2023.logic.map :as board]))

(def test-game
  {:game {:id "Battle of Titans"
          :version 1
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
                        :offset [0 0]}}}})