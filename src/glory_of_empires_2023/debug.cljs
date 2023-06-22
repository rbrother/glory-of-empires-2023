(ns glory-of-empires-2023.debug
  (:require [re-frame.core :as rf]
            [cljs-time.core :as cljs-time]
            [medley.core :refer [assoc-some]]
            [clojure.data :refer [diff]]
            [glory-of-empires-2023.logic.malli :as malli]
            [malli.core :as m]
            [malli.util :as mu]))

(defn current-time-str []
  (let [t (js/Date.)]
    (str (.getHours t) ":" (.getMinutes t) ":" (.getSeconds t))))

(defn log [item] (.log js/console item))

(defn log-error [item] (.error js/console item))

(defn log-color [item color]
  (.log js/console (str "%c" item) (str "color: " color)))

(defn with-timing [title fn]
  (let [now (cljs-time/now)
        result (fn)
        end (cljs-time/now)
        dur (cljs-time/interval now end)]
    (log (str title " duration " (cljs-time/in-millis dur) " ms"))
    result))

(def log-event
  (rf/->interceptor
    :id :debug
    :before identity
    :after (fn [{:keys [coeffects effects] :as context}]
             (let [orig-db (get coeffects :db)
                   after-db (get effects :db)
                   [only-in-orig only-in-after _] (diff orig-db after-db)]
               (log-color (str "[EVENT " (current-time-str) "] " (first (:event coeffects))) "blue")
               (when only-in-orig (log {:removed only-in-orig}))
               (when only-in-after (log {:added only-in-after})))
             context ;; Logging is side effect: return the context unaltered
             )))

(def compiled-appdb-malli (m/explainer malli/app-db))

(def validate-malli
  (rf/->interceptor
    :id :validate
    :before identity
    :after (fn [{:keys [coeffects effects] :as context}]
             (let [orig-db (get coeffects :db)
                   after-db (get effects :db)
                   error (first (:errors (compiled-appdb-malli after-db)))]
               (if error
                 (do
                   (log-color (str "[VALIDATION FAILURE] " (first (:event coeffects))
                                "\n" error) "red")
                   (assoc-in context [:effects :db] orig-db)) ;; ignore changes if errors
                 context)))))
