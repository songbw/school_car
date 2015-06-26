(ns school-car.weixin.controllers.weixins
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            [clojure.xml :as pxml]
            [clojure.zip :as pzip]
            [taoensso.carmine :as car :refer (wcar)])
  (:require [school-car.weixin.service.weixin :as sweixin]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log]
            [clojure.data.json :as json2])
  (:use [compojure.core]
        [utils.web]
        [clojure.java.io :only [input-stream]])
  (:import java.security.MessageDigest))

(def server1-conn {:pool {} :spec {:host (common/getParam "redis_host" "182.92.186.153") :port 6379}})
(defmacro wcar*
  [& body]
  `(car/wcar server1-conn ~@body))

(defn write-token
  []
  (let [token (common/http-get "https://api.weixin.qq.com/cgi-bin/token" {:grant_type "client_credential" :appid "wx40faaf90491d548c" :secret "8a16ad8803263f31e684eca15bad6241"})]
    (log/info "token ------------   " token)
    (if-let [access_token (:access_token (json2/read-str (:body token) :key-fn keyword))]
      (wcar* (car/ping) (car/setex "token" (:expires_in (json2/read-str (:body token) :key-fn keyword)) access_token)))
    (:access_token (json2/read-str (:body token) :key-fn keyword))))

(defn read-token
  []
  (if-let [token (get (wcar* (car/ping) (car/get "token")) 1)]
    token
    (write-token)
    ))

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

(defn post-signature
  [req]
  (log/info "post msg is *********   " req)
  (let [body (:body req)
        zip-file (pzip/xml-zip (pxml/parse (input-stream body)))
        send-message (sweixin/send-message zip-file)]
    (resp/content-type (resp/response send-message) "application/xml; charset=utf-8")))

(def menu-body {:button [{:name "发布产品" :type "view" :url "http://localhost:3000/list"} {:name "我的" :sub_button [{:type "view" :name "绑定手机号" :url "http://localhost:3000/binding"} {:type "click" :name "解除绑定" :key "V1001"}]}]})

(defn add-menu
  [req]
  (let [token (read-token)
        back (common/http-post (str " https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" token) menu-body)]
    (resp/response back)))
