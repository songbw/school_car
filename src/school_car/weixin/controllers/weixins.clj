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

(def xml-data [{:tag :xml, :attrs nil, :content [{:tag :ToUserName, :attrs nil, :content ["gh_6db043859c65"]} {:tag :FromUserName, :attrs nil, :content ["o58lCtx6refDawv5fCe6uzbzj_Js"]} {:tag :CreateTime, :attrs nil, :content ["1434896687"]} {:tag :MsgType, :attrs nil, :content ["text"]} {:tag :Content, :attrs nil, :content ["呜呜呜"]} {:tag :MsgId, :attrs nil, :content ["6162834344014993564"]}]} nil])

(def xml-tag [:ToUserName :FromUserName :CreateTime :MsgType :Content :MsgId])

;;xml file convert to key and value
(defn xml2key-value
  [xml-data tag]
  (first (for [x xml-data
               :when (= tag (:tag x))]
           [(keyword tag) (first (:content x))])))

;;xml convert to json
(defn xml2json
  [xml-data xml-tag]
  (into {}
        (map xml2key-value
             (into []
                   (repeat 9 (:content (first xml-data))))
             xml-tag)))

;; xml to json
(defn x2j
  [xml-data]
  (let [contents (-> xml-data first :content)]
    (reduce
      (fn [result {:keys [tag content]}]
        (assoc result tag (first content)))
      {} contents)))

(def text-temple "<xml><ToUserName>%s</ToUserName><FromUserName>%s</FromUserName><CreateTime>%s</CreateTime><MsgType>text</MsgType><Content>%s</Content></xml>")

(defn post-signature
  [req]
  (log/info "post msg is *********   " req)
  (let [body (:body req)
        zip-file (pzip/xml-zip (pxml/parse (input-stream body)))
        body-json (x2j zip-file)]
    (log/info "body is ****** " body  "\n xml-file is " zip-file "\n body-json is " body-json "\n")

    (resp/content-type (resp/response (format text-temple (:FromUserName body-json) (:ToUserName body-json) (:CreateTime body-json) (:Content body-json))) "application/xml; charset=utf-8")
    ))
