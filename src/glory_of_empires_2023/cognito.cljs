(ns glory-of-empires-2023.cognito
  (:require
    [re-frame.core :refer [reg-event-db]]))

;; Example hosted UI URL:
;; https://<your_domain>/oauth2/authorize?response_type=code&client_id=<your_app_client_id>&redirect_uri=<your_callback_url>

;; In our case:
;; https://glory-of-empires.auth.eu-north-1.amazoncognito.com/login?client_id=4ns3f360obk6e8ne8e4t7c9fde&response_type=code&scope=email+openid+phone&redirect_uri=http%3A%2F%2Flocalhost%3A8280

;; Redirecting back to eg:  http://localhost:8280/?code=dc7dfe5b-b1e2-4eea-a85c-514e13da722c

(def config
  {:region "eu-north-1"
   :account-id "886559219659"
   :app-client-id "4ns3f360obk6e8ne8e4t7c9fde"
   :identity-pool-id "eu-north-1:434228c0-d69b-4dd3-93be-65105e8ef28b"
   })

(defn redirect-to-cognito-login []
  (let [redirect-url (-> js/window (.-location) (.-origin))
        login-url (str "https://glory-of-empires.auth.eu-north-1.amazoncognito.com/login?"
                    "client_id=" (:app-client-id config) "&"
                    "response_type=code&"
                    "scope=email+openid+phone&"
                    "redirect_uri=" (js/encodeURIComponent redirect-url))]
    (.log js/console login-url)
    (-> js/window (.-location) (.replace login-url))))

(defn get-credentials-for-code [cognito-code]
  (.log js/console "get-credentials-for-code" cognito-code))

;; events

(reg-event-db ::login
  (fn [db _]
    (let [url-pars (new js/URLSearchParams (-> js/window (.-location) (.-search)))
          cognito-code (.get url-pars "code")]
      (if cognito-code
        (get-credentials-for-code cognito-code)
        (redirect-to-cognito-login)))))