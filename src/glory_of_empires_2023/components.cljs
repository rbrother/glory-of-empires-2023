(ns glory-of-empires-2023.components
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db]]
    [glory-of-empires-2023.debug :as debug]))

(def image-dir "https://rjb-share.s3.eu-north-1.amazonaws.com/glory-of-empires-pics/")

(defn dialog [{:keys [title] :as opt} & content]
  [:div.dialog-screen {:id "dialog-background"
                       :on-click #(dispatch [::click-background (-> % .-target .-id)])}
   [:div.dialog
    (when title [:div.dialog-title title])
    (into [:div.dialog-content-wrap] content)]])

;; events

(reg-event-db ::click-background
  (fn [db [_ target]]
    ;; Background receives click through bubbling even when the dialog
    ;; itself is clicked. We close the dialog only if the target is
    ;; actually the background and not the dialog.
    (cond-> db
      (= target "dialog-background") (dissoc :dialog))))