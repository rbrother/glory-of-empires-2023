(ns glory-of-empires-2023.debug
  (:require [re-frame.core :as rf]
            [clojure.data :refer [diff]]))

(defn log [item] (.log js/console item))

(defn log-color [item color]
  (.log js/console (str "%c" item) (str "color: " color)))

(def log-event
  (rf/->interceptor
    :id :debug
    :before identity
    :after (fn [{:keys [coeffects effects] :as context}]
             (let [orig-db (get coeffects :db)
                   after-db (get effects :db)
                   [only-in-orig only-in-after _] (diff orig-db after-db)]
               (log-color (str "[EVENT] " (first (:event coeffects))) "blue")
               (when only-in-orig (log {:removed only-in-orig}))
               (when only-in-after (log {:added only-in-after})))
             context ;; Logging is side effect: return the context unaltered
             )))
