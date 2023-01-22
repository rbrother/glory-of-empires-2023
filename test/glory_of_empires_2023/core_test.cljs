(ns glory-of-empires-2023.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [glory-of-empires-2023.data.tiles :as tiles]))

(deftest tiles-test
  (is (= 290
        (count tiles/all-systems-list))))
