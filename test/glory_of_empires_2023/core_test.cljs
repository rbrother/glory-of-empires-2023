(ns glory-of-empires-2023.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [glory-of-empires-2023.logic.tiles :as tiles]
            [glory-of-empires-2023.logic.utils :as utils]
            [glory-of-empires-2023.logic.map :as board]
            [glory-of-empires-2023.logic.ships :as ships]
            [cljs.pprint :refer [pprint]]))

(deftest utils-test
  (is (= [{:owner :mentak, :location :b3, :offset [0 0], :id :dr1}
          {:owner :yssaril, :location :a3, :offset [50 20], :id :cr2}]
         (utils/vals-with-id
           {:dr1 {:owner :mentak,
                  :location :b3
                  :offset [0 0]}
            :cr2 {:owner :yssaril
                  :location :a3
                  :offset [50 20]}}))))

(deftest tiles-test
  (is (= 290
         (count tiles/all-systems-list))))

(deftest create-board-test
  (is (= (board/round-board 3)
         {:d1 {:logical-pos [1 -2], :system :setup-light-blue, :id :d1},
          :e1 {:logical-pos [2 -2], :system :setup-light-blue, :id :e1},
          :b2 {:logical-pos [-1 -1], :system :setup-light-blue, :id :b2},
          :d2 {:logical-pos [1 -1], :system :setup-yellow, :id :d2},
          :c3 {:logical-pos [0 0], :system :setup-red, :id :c3},
          :e2 {:logical-pos [2 -1], :system :setup-light-blue, :id :e2},
          :e3 {:logical-pos [2 0], :system :setup-light-blue, :id :e3},
          :a3 {:logical-pos [-2 0], :system :setup-light-blue, :id :a3},
          :c4 {:logical-pos [0 1], :system :setup-yellow, :id :c4},
          :d4 {:logical-pos [1 1], :system :setup-light-blue, :id :d4},
          :c2 {:logical-pos [0 -1], :system :setup-yellow, :id :c2},
          :c1 {:logical-pos [0 -2], :system :setup-light-blue, :id :c1},
          :b4 {:logical-pos [-1 1], :system :setup-yellow, :id :b4},
          :a5 {:logical-pos [-2 2], :system :setup-light-blue, :id :a5},
          :b3 {:logical-pos [-1 0], :system :setup-yellow, :id :b3},
          :c5 {:logical-pos [0 2], :system :setup-light-blue, :id :c5},
          :d3 {:logical-pos [1 0], :system :setup-yellow, :id :d3},
          :b5 {:logical-pos [-1 2], :system :setup-light-blue, :id :b5},
          :a4 {:logical-pos [-2 1], :system :setup-light-blue, :id :a4}}))
  (is (= (board/rect-board 5 3)
         {:e1 {:logical-pos [2 -2], :system :setup-light-blue, :id :e1},
          :d2 {:logical-pos [1 -1], :system :setup-yellow, :id :d2},
          :c3 {:logical-pos [0 0], :system :setup-red, :id :c3},
          :e2 {:logical-pos [2 -1], :system :setup-light-blue, :id :e2},
          :e3 {:logical-pos [2 0], :system :setup-light-blue, :id :e3},
          :a3 {:logical-pos [-2 0], :system :setup-light-blue, :id :a3},
          :c4 {:logical-pos [0 1], :system :setup-yellow, :id :c4},
          :c2 {:logical-pos [0 -1], :system :setup-yellow, :id :c2},
          :b4 {:logical-pos [-1 1], :system :setup-yellow, :id :b4},
          :a5 {:logical-pos [-2 2], :system :setup-light-blue, :id :a5},
          :b3 {:logical-pos [-1 0], :system :setup-yellow, :id :b3},
          :d3 {:logical-pos [1 0], :system :setup-yellow, :id :d3},
          :a4 {:logical-pos [-2 1], :system :setup-light-blue, :id :a4}})))

(deftest add-ships-test
  (is (= (ships/create-ships
           {:fi3 {:type :fi, :owner :norr, :location :b3, :offset [129 -8]}}
           {:fi 2, :de 1, :cr 2}
           {:id :b3, :logical-pos [0 1], :system :amun}
           nil
           :mentak)
         {:fi3 {:type :fi, :owner :norr, :location :b3, :offset [129 -8]},
          :fi4 {:type :fi, :owner :mentak, :location :b3, :hits-taken 0, :id :fi4, :offset [79 -18]},
          :fi5 {:type :fi, :owner :mentak, :location :b3, :hits-taken 0, :id :fi5, :offset [119 -58]},
          :de1 {:type :de, :owner :mentak, :location :b3, :hits-taken 0, :id :de1, :offset [-181 2]},
          :cr1 {:type :cr, :owner :mentak, :location :b3, :hits-taken 0, :id :cr1, :offset [-101 142]},
          :cr2 {:type :cr, :owner :mentak, :location :b3, :hits-taken 0, :id :cr2, :offset [-141 82]}})))

