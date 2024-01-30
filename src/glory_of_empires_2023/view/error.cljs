(ns glory-of-empires-2023.view.error
  (:require
    [re-frame.core :refer [subscribe dispatch reg-event-db reg-sub]]))

(defn error-message []
  (let [{:keys [type message time operation]} @(subscribe [::error])]
    (when type
      [:div.error
       [:span time ": Failed " operation ": " type ". See log for details"]
       [:span [:button {:on-click #(dispatch [::dismiss])} "Dismiss"]]])))

;; subs

(reg-sub ::error (fn [db _] (:aws-error db)))

;; events

(reg-event-db ::dismiss
  (fn [db _]
    (dissoc db :aws-error)))