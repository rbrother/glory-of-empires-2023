(ns glory-of-empires-2023.aws.dynamo-db
  (:require [glory-of-empires-2023.aws.core :as aws]
            [glory-of-empires-2023.debug :refer [log]]))

(defn- dynamo-db [db]
  (new (.-DynamoDB js/AWS)
    #js {"region" (:region aws/config)
         "credentials" (aws/credentials-object db)}))

(defn get-game [db game-id callback-fn]
  (-> (dynamo-db db)
    (.getItem (clj->js
                {"TableName" "glory-of-empires"
                 "Key" {"id" {"S" game-id}}})
      (fn [err ^js/Object data]
        (if err
          (do
            (log err)
            (log (.-stack err)))
          (let [item (.-Item data)
                ;; Convert {"N": "767} -> 767
                unmarshalled (-> js/AWS (.-DynamoDB) (.-Converter) (.unmarshall item))
                clj (js->clj unmarshalled :keywordize-keys true)]
            (log data)
            (callback-fn clj)))))))