(ns glory-of-empires-2023.aws.dynamo-db
  (:require
    [glory-of-empires-2023.logic.json :as json]
    [glory-of-empires-2023.aws.core :as aws]
    [glory-of-empires-2023.debug :refer [log]]))

;; DynamoDB pricing (AWS Stockholm)
;; Data first 25 GB Free
;; $0.269 per million read request units (reads 1/s -> 0.02$ per day)
;; Data transfer out: 100 GB/month free across all AWS services

(defn- dynamo-db ^js/AWS.DynamoDB [db]
  (new (.-DynamoDB js/AWS)
    #js {"region" (:region aws/config)
         "credentials" (aws/credentials-object db)}))

(def converter (-> js/AWS (.-DynamoDB) (.-Converter)))

(defn marshall-to-ddb-item ^js/Object [cljs-data] ;; Convert 767 -> {"N": "767}
  (-> converter (.marshall (clj->js (json/keywords-to-json cljs-data)))))

(defn unmarshall-from-ddb-item [ddb-object] ;; Convert {"N": "767} -> 767
  (-> converter
    (.unmarshall ddb-object)
    (js->clj :keywordize-keys true)
    (json/keywords-from-json)))

(defn get-item [app-db table-name key opts data-callback]
  (-> (dynamo-db app-db)
    (.getItem (clj->js
                (merge {"TableName" table-name
                        "Key" key}
                  opts))
      (aws/result-handler "Get Item from Database"
        (fn [^js/Object data]
          (data-callback (unmarshall-from-ddb-item (.-Item data))))))))

(defn put-item [app-db table-name item opts result-callback]
  (let [params (clj->js
                 (merge {"TableName" table-name
                    "ReturnConsumedCapacity" "TOTAL"
                    "Item" (marshall-to-ddb-item item)}
                   opts))
        handler (aws/result-handler "Save Item to Database"
                  (fn [^js/Object data]
                    (result-callback (js->clj data :keywordize-keys true))))]
    (log params)
    ^js/Object (.putItem (dynamo-db app-db) params handler)))

(defn get-game [app-db game-id callback-fn]
  (get-item app-db "glory-of-empires" {"id" {"S" game-id}} {} callback-fn))

(defn get-game-version [app-db game-id callback-fn]
  (get-item app-db "glory-of-empires" {"id" {"S" game-id}}
    {"ProjectionExpression" "id, version"}
    callback-fn))

(defn save-game [app-db game expected-db-version callback-fn]
  (put-item app-db "glory-of-empires" game
    {"ConditionExpression" "version = :version"
     "ExpressionAttributeValues" {":version" {"N" (str expected-db-version)}}}
    callback-fn))