(ns glory-of-empires-2023.aws.core
  (:require
    [glory-of-empires-2023.debug :as debug]
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx reg-sub]]
    [glory-of-empires-2023.debug :refer [log log-error]]))

(def config {:region "eu-north-1"
             :account-id "886559219659"})

(defn credentials-object [db] ;; This should only be called after login and creation of the credentials-object
  (get-in db [:login :credentials-object]))

(defn handle-error [operation-name err]
  (log-error err)
  (dispatch [::error {:type (.-code err)
                      :message (.-message err)
                      :time (.toLocaleTimeString (.-time err))
                      :operation operation-name}]))

(defn result-handler [operation-name data-callback]
  (fn [err ^js/Object data]
    (if err
      (handle-error operation-name err)
      (data-callback data))))

;; Events

(reg-event-db ::error [debug/log-event debug/validate-malli]
  (fn [db [_ info]]
    (assoc db :aws-error info)))

(def mins-30 (* 1000 60 30))

(reg-event-fx ::renew-credentials [debug/log-event debug/validate-malli]
  (fn [{db :db} _]
    ;; It is not fully clear if this allows extension of the credentials beyond the ID-token limit
    ;; since it is still based on the same ID token
    (.refresh (credentials-object db)
              (fn [err]
                (if err (handle-error "Refresh Credentials" err)
                        (log "Credentials successfully renewed"))))
    {:db db
     :dispatch-later {:ms mins-30 :dispatch [::renew-credentials]}}))
