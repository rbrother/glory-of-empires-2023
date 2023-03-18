(ns glory-of-empires-2023.aws.core
  (:require [glory-of-empires-2023.debug :refer [log]]))

(def config {:region "eu-north-1"
             :account-id "886559219659"})

(defn credentials-object-from-token [{{:keys [id-token]} :login :as db}]
  (js/CognitoIdentityCredentials id-token))

(defn result-handler [data-callback]
  (fn [err ^js/Object data]
    (if err
      (do
        (log err)
        (log (.-stack err)))
      (data-callback data))))