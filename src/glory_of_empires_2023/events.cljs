(ns glory-of-empires-2023.events
  (:require
    [re-frame.core :refer [reg-event-db]]))

(reg-event-db ::initialize-db
  (fn [_ _] {}))
