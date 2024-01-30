(ns glory-of-empires-2023.aws.dynamo-db
  (:require
    [medley.core :refer [assoc-some]]
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

(defn condition-failed? [err]
  (= (.-code err) "ConditionalCheckFailedException"))

(defn put-item [{:keys [app-db table-name item result-callback condition-failed-callback
                        condition-expression expression-attribute-values]}]
  (let [params (clj->js
                 (assoc-some {}
                             "TableName" table-name
                             "ReturnConsumedCapacity" "TOTAL"
                             "Item" (marshall-to-ddb-item item)
                             "ConditionExpression" condition-expression
                             "ExpressionAttributeValues" expression-attribute-values))
        handler (fn [err ^js/Object data]
                  (cond
                    (not err) (result-callback (js->clj data :keywordize-keys true))
                    (condition-failed? err) (condition-failed-callback)
                    :else (aws/handle-error "Save Item to Database" err)))]
    ^js/Object (.putItem (dynamo-db app-db) params handler)))

(def game-table-name "glory-of-empires")

(defn get-game [app-db game-id callback-fn]
  (get-item app-db game-table-name {"id" {"S" game-id}} {} callback-fn))

(defn get-game-version [app-db game-id callback-fn]
  (get-item app-db game-table-name {"id" {"S" game-id}}
            {"ProjectionExpression" "id, version"}
            callback-fn))

(defn save-game [app-db game expected-db-version callback-fn remote-newer-callback]
  (put-item {:app-db app-db, :table-name "glory-of-empires"
             :item game, :result-callback callback-fn
             ;; Require that the version of the game before overwrite is same as we locally think
             ;; it is ie. other clients have not been pushing changes that we would overwrite
             :condition-expression "version = :version"
             :expression-attribute-values {":version" {"N" (str expected-db-version)}}
             :condition-failed-callback remote-newer-callback}))