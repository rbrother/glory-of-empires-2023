(ns glory-of-empires-2023.aws.dynamo-db
  (:require [glory-of-empires-2023.aws.core :as aws]
            [glory-of-empires-2023.aws.cognito :as cognito]))

(defn- dynamo-db [db]
  (new (.-DynamoDB js/AWS)
    #js {"region" (:region aws/config)
         "credentials" (cognito/credentials-object-from-token db)}))

(defn get-item [app-db table-name key data-callback]
  (-> (dynamo-db app-db)
    (.getItem (clj->js
                {"TableName" table-name
                 "Key" key})
      (aws/result-handler
        (fn [^js/Object data]
          (let [item (.-Item data)
                ;; Convert {"N": "767} -> 767
                unmarshalled (-> js/AWS (.-DynamoDB) (.-Converter) (.unmarshall item))]
            (data-callback
              (js->clj unmarshalled :keywordize-keys true)))))))
  )
(defn get-game [app-db game-id callback-fn]
  (get-item app-db "glory-of-empires" {"id" {"S" game-id}} callback-fn))