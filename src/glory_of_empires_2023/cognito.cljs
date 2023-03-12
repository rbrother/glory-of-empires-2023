(ns glory-of-empires-2023.cognito
  (:require
    [re-frame.core :refer [reg-event-db]]
    [clojure.string :as str]
    ["jwt-decode" :as jwt-decode]
    [medley.core :refer [map-keys]]
    [camel-snake-kebab.core :as csk]
    [glory-of-empires-2023.debug :as debug :refer [log]]))

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
   ;; "code" for code grant flow and "token" for implicit flow:
   ;; https://aws.amazon.com/blogs/mobile/understanding-amazon-cognito-user-pool-oauth-2-0-grants/
   :response-type "token"
   :scope "email+openid+phone"
   })

(defn decode-token [token-str]
  (map-keys csk/->kebab-case
    (js->clj (jwt-decode token-str) :keywordize-keys true)))

;; Returns map the param-names as keys and param valus
(defn url-params [search-string param-definitions]
  (let [url-pars (new js/URLSearchParams search-string)]
    (->> param-definitions
      (map (fn [{:keys [key par]}] [key (.get url-pars par)] ))
      (into {}))))

;; http://localhost:8280/#id_token=eyJraWQiOiJvZzZEdUdKTjNLUkgybk5UcTk2ZEtHK1h6NUFlTUhDYVR1WEN6enNBK3k4PSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoidEVZblFGbzBqX1ZibTQ5OEdfdnJzQSIsInN1YiI6IjQ0MzkxMjNjLWNkMzctNGZkNy1hMmRkLWNlOTQxODUwYjMwYyIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtbm9ydGgtMS5hbWF6b25hd3MuY29tXC9ldS1ub3J0aC0xX1l0ZzZKa095OCIsImNvZ25pdG86dXNlcm5hbWUiOiI0NDM5MTIzYy1jZDM3LTRmZDctYTJkZC1jZTk0MTg1MGIzMGMiLCJhdWQiOiI0bnMzZjM2MG9iazZlOG5lOGU0dDdjOWZkZSIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjc4NjA0NjA0LCJleHAiOjE2Nzg2MDgyMDQsImlhdCI6MTY3ODYwNDYwNCwianRpIjoiZWRhNzFhOWQtNTA1YS00YmM5LWE3YzQtYzNmNTYyN2NkNDhiIiwiZW1haWwiOiJyb2JlcnRAaWtpLmZpIn0.AwaZDLrEoiY72evb5bFZpE9sp3h16kUpWMpW3v96KRrwOqxjMtq2dBmFcvTAYipaB_Sox76bx4WsC9SvFoVlEDEoKNVSann-OSEZeLSOKYADccVqiZDoUY6XT3YpnJsBIc98upywh4G2o_0iD_FbePQkWBcctg8iSR-9uoMm7wuzxaY5nijkOKh3s9ZE4P_GnvBwWjMUkbKePo-v6Z0ZJ_WPmoySZdT3EhDhpONZlQ8CqhUXU4pdkvP8mIqgG6pXYe6r1QZz-BHIINl19h4HCl_S--97CBcZLqL6JVtWNA6K7Kdko4zDjT0k_0_-yGwQHKxoLtRrH8w01sk3ppZ0sQ
;; &access_token=eyJraWQiOiJhODh2aGVrTm9VTktOcjczNER6bUhrSnE2WFVudmtGdnM3dDRRNm1LSHRnPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI0NDM5MTIzYy1jZDM3LTRmZDctYTJkZC1jZTk0MTg1MGIzMGMiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6InBob25lIG9wZW5pZCBlbWFpbCIsImF1dGhfdGltZSI6MTY3ODYwNDYwNCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LW5vcnRoLTEuYW1hem9uYXdzLmNvbVwvZXUtbm9ydGgtMV9ZdGc2SmtPeTgiLCJleHAiOjE2Nzg2MDgyMDQsImlhdCI6MTY3ODYwNDYwNCwidmVyc2lvbiI6MiwianRpIjoiNTQ2ODI0MGItOTAzNC00NmI5LTllZjctMjdkZTAxNzIyZTI3IiwiY2xpZW50X2lkIjoiNG5zM2YzNjBvYms2ZThuZThlNHQ3YzlmZGUiLCJ1c2VybmFtZSI6IjQ0MzkxMjNjLWNkMzctNGZkNy1hMmRkLWNlOTQxODUwYjMwYyJ9.DixCFgdinD3eD4op6EIFF9x_exYBMFDJFwWDFnXpsuBswsuwj5WY6bdGvGveW8aEUOerqbfcdODBdnj_j4FisvpC-xZp2HojJUEvyeuVOz8Em2EQpliusKAQWYkGPs1UpQ_uNZcsq0rNenELSjGHlSIsCbPLkZS_EU_DuyDikee-CnmKo8xZzq-SRTdXQwIrtplG57XuLsc5asL2bPdUN677vM_UgHEotg4keYvnURpiiI04NHpNe58R3ihiAzQjh4EsindlYGbQVgEFBLC1N8lxwFMhrauDhjQ_BbWbeQ03IYA_Rz4eigrb1J01x87npk6Mt7pmImfrEn67MyB-0A
;; &expires_in=3600
;; &token_type=Bearer

(def login-pars
  [{:key :id-token, :par "id_token"}
   {:key :access-token, :par "access_token"}])

(defn token-params []
  (let [url (-> js/window (.-location) (.-href))
        [_ params-str] (str/split url #"#")]
    (url-params params-str login-pars)))

(defn redirect-to-cognito-login []
  (let [redirect-url (-> js/window (.-location) (.-origin))
        login-url (str "https://glory-of-empires.auth.eu-north-1.amazoncognito.com/login?"
                    "client_id=" (:app-client-id config) "&"
                    "response_type=" (:response-type config) "&"
                    "scope=" (:scope config) "&"
                    "redirect_uri=" (js/encodeURIComponent redirect-url))]
    (log login-url)
    (-> js/window (.-location) (.replace login-url))))

(defn get-credentials-for-code [db {:keys [id-token access-token] :as tokens}]
  (let [credentials (new (-> js/AWS (.-CognitoIdentityCredentials))
                      #js {"IdentityPoolId" (:identity-pool-id config)})]
    (log credentials)
    (set! (-> js/AWS (.-config) (.-region)) (:region config))
    (set! (-> js/AWS (.-config) (.-credentials)) credentials)
    ;; How to set the token to credentials?

    (-> js/window (.-history) (.pushState "" "" "/")) ;; Remove the token from URL
    (assoc db :login
      (assoc tokens
        :id (decode-token id-token)
        :access (decode-token access-token)))))

;; events

(reg-event-db ::login [debug/log-event]
  (fn [db _]
    (let [tokens (token-params)]
      (if (:id-token tokens)
        (get-credentials-for-code db tokens)
        (redirect-to-cognito-login)))))