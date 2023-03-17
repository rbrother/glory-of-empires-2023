(ns glory-of-empires-2023.aws.core)

(def config {:region "eu-north-1"
             :account-id "886559219659"})
(defn credentials-object [{{credentials :credentials} :login :as db}]
  (let [{:keys [access-key-id secret-key session-token]} credentials]
    (new (.-Credentials js/AWS) access-key-id secret-key session-token)))

