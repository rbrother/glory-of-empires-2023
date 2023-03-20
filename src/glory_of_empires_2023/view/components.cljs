(ns glory-of-empires-2023.view.components
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.debug :as debug]))

(def image-dir "https://rjb-share.s3.eu-north-1.amazonaws.com/glory-of-empires-pics/")

(defn handler-no-propagate [dispatch-vector]
  (fn [event]
    (dispatch dispatch-vector)
    (.stopPropagation event) ;; prevent eg. click being handled by parents also
    ))

(defn ok-cancel [dispatch-ok dispatch-cancel]
  [:div.flex {:style {:justify-content "center"}}
   [:button.ok {:on-click (handler-no-propagate dispatch-ok)} "OK"]
   [:button.cancel {:on-click (handler-no-propagate dispatch-cancel)} "Cancel"]])

(defn dialog [{:keys [title] :as opt} & content]
  [:div.dialog-screen {:id "dialog-background"
                       :on-click #(dispatch [::click-background (-> % .-target .-id)])}
   [:div.dialog
    (when title [:div.dialog-title title])
    (into [:div.dialog-content-wrap] content)]])

(defn menu-item [text dispatch-vector]
  [:div.menu-item
   {:on-click (handler-no-propagate dispatch-vector)}
   text])

;; events

(reg-event-db ::click-background [debug/log-event debug/validate-malli]
  (fn [db [_ target]]
    ;; Background receives click through bubbling even when the dialog
    ;; itself is clicked. We close the dialog only if the target is
    ;; actually the background and not the dialog.
    (cond-> db
      (= target "dialog-background") (dissoc :dialog))))