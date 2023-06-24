(ns glory-of-empires-2023.view.ship-menu
  (:require [clojure.string :as str]
            [glory-of-empires-2023.game-sync :as game-sync]
            [re-frame.core :refer [subscribe dispatch reg-event-db reg-event-fx
                                   reg-sub inject-cofx]]
            [medley.core :refer [dissoc-in]]
            [glory-of-empires-2023.logic.ships :as ships]
            [glory-of-empires-2023.debug :as debug]
            [glory-of-empires-2023.subs :as subs]
            [glory-of-empires-2023.view.components :refer [image-dir] :as comp]))

(defn view [ship-id]
  (let [{ship-name :name
         :keys [type-name owner-name owner speed
                hits-taken max-hit-points fire-count fire-percent] :as unit}
        @(subscribe [::subs/amended-unit ship-id])
        hit-points (- max-hit-points hits-taken)]
    (.log js/console unit)
    [:div.tile-menu-wrap {:style {:left 100, :top 0}}
     [:div.menu {:style {:min-width "240px"}}
      [:div.menu-title
       [:span "Unit " (str/upper-case (name ship-id))]
       [:img {:src (str image-dir "Flags/Flag-LgRnd-" (str/capitalize (name owner)) ".png")}]]
      [:div {:style {:display "grid", :grid-template-columns "auto auto"
                     :grid-row-gap "8px" :grid-column-gap "8px"
                     :padding "8px"}}
       [:div.bold "Type"] [:div type-name]
       [:div.bold "Name"] [:div (when ship-name (str "'" ship-name "'"))]
       [:div.bold "Owner"] [:div owner-name]
       [:div.bold "Attack"] [:div (when (> fire-count 1) (str fire-count " x "))
                             (when fire-percent (str fire-percent "%"))]
       [:div.bold "Speed"] [:div speed]
       (when (> max-hit-points 1)
         [:<>
          [:div.bold "Hitpoints"] [:div hit-points " / " max-hit-points]])]
      (when (> hit-points 1)
        [comp/menu-item "Inflict Hit" [::add-hit-ship ship-id]])
      (when (> hits-taken 0)
        [comp/menu-item "Repair" [::repair-ship ship-id]])
      [comp/menu-item "Destroy" [::delete-ship ship-id]]]]))

;; helpers

(defn update-unit [fx id fn]
  (game-sync/update-game fx
    #(update-in % [:units id] fn)))

;; events

(reg-event-fx ::delete-ship [debug/log-event debug/validate-malli]
  (fn [fx [_ id]]
    (game-sync/update-game fx
      (fn [game] (dissoc-in game [:units id])))))

(reg-event-fx ::add-hit-ship [debug/log-event debug/validate-malli]
  (fn [fx [_ id]]
    (update-unit fx id ships/inflict-hit)))

(reg-event-fx ::repair-ship [debug/log-event debug/validate-malli]
  (fn [fx [_ id]]
    (update-unit fx id #(assoc % :hits-taken 0))))
