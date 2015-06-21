(ns school-car.weixin.controllers.weixins
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            [clj-http.client :as client]
            [clojure.xml :as pxml]
            [clojure.zip :as pzip]
            [taoensso.carmine :as car :refer (wcar)])
  (:require [school-car.weixin.models.weixin :as weixin]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log]
            [clojure.data.json :as json2])
  (:use [compojure.core]
        [utils.web]
        [clojure.java.io :only [input-stream]])
  (:import java.security.MessageDigest))

(defn http-get
  [url params]
  (let [resp (client/get url
                         {:query-params params :debug true})]
    resp))

(def server1-conn {:pool {} :spec {:host (common/getParam "redis_host" "127.0.0.1") :port 6379}})
(defmacro wcar*
  [& body]
  `(car/wcar server1-conn ~@body))

(defn write-token
  []
  (let [token (http-get "https://api.weixin.qq.com/cgi-bin/token" {:grant_type "client_credential" :appid "wx40faaf90491d548c" :secret "8a16ad8803263f31e684eca15bad6241"})]
    (log/info "token ------------   " token)
    (if-let [access_token (:access_token (json2/read-str (:body token) :key-fn keyword))]
      (wcar* (car/ping) (car/setex "token" (:expires_in (json2/read-str (:body token) :key-fn keyword)) access_token)))
    (:access_token (json2/read-str (:body token) :key-fn keyword))))

(defn read-token
  []
  (if-let [token (wcar* (car/ping) (car/get "token"))]
    token
    (write-token)))

(defn login-weixin
  [req]
  (log/info "login-weixin  **********************     ")
  (resp/response (write-token)))

(def sign-key [:signature :timestamp :nonce :echostr])

(defn check-signature
  [req]
  (log/info (:params req))
  (log/info (select-keys (:params req) sign-key))
  (resp/response (:echostr (:params req)))
  )

(defn escape-xml
  [text]
 ;; (log/info "escape - xml -------------> " text)
  (clojure.string/replace text #"&lt;|&gt;" {"&lt;" "<" "&gt;" ">"}))

(defn- parse-aotuid
  [post-wlmq node]
  (let [body (get post-wlmq :body)
        escapebody (str (escape-xml body))
        xml-file (pxml/parse (java.io.ByteArrayInputStream. (.getBytes escapebody)))]
    (first (for [x (xml-seq xml-file)
                 :when (= node (:tag x))]
             [(keyword node) (first (:content x))]))))

(defn post-signature
  [req]
  (log/info "post msg is *********   " req)
  (let [body (:body req)
       ;; escapebody (str (escape-xml body))
        zip-file (pzip/xml-zip (pxml/parse (input-stream body)))
        ]
    (log/info "body is ****** " body  "\n xml-file is " zip-file)))
