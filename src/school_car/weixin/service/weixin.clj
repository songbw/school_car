(ns school-car.weixin.service.weixin
  (:require [clojure.string :as str]
            [utils.common :as common]
            [utils.log :as log]
            [taoensso.carmine :as car :refer (wcar)]))

(def text-receive "<xml><ToUserName>%s</ToUserName><FromUserName>%s</FromUserName><CreateTime>%s</CreateTime><MsgType>text</MsgType><Content>%s</Content></xml>")

(def image-receive "<xml><ToUserName>%s</ToUserName><FromUserName>%s</FromUserName><CreateTime>%s</CreateTime><MsgType>image</MsgType><Image><MediaId>%s</MediaId></Image></xml>")

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

(def server1-conn {:pool {} :spec {:host (common/getParam "redis_host" "182.92.186.153") :port 6379}})
(defmacro wcar*
  [& body]
  `(car/wcar server1-conn ~@body))

(defn get-by-redis
  [redis-key]
  (get (wcar* (car/ping) (car/get redis-key)) 1))

(defn send-message
  [zip-file]
  (let [body-json (x2j zip-file)]
    (log/info "body-json is -----\n" body-json)
    (cond
     (= "text" (:MsgType body-json))
     (if-let [redis-value (get-by-redis (:FromUserName body-json))]
       (do
         (wcar* (car/ping) (car/setex (:FromUserName body-json) 600 "解绑成功，感谢您对我们的支持，欢迎您再次使用！"))
         (format text-receive (:FromUserName body-json) (:ToUserName body-json) (:CreateTime body-json) redis-value))
       (format text-receive (:FromUserName body-json) (:ToUserName body-json) (:CreateTime body-json) "欢迎使用魏小店，我们将竭诚为您服务，请选择下方的菜单进行操作！")
       )
     (= "image" (:MsgType body-json)) (format image-receive (:FromUserName body-json) (:ToUserName body-json) (:CreateTime body-json) (:MediaId body-json))
     (and (= "event" (:MsgType body-json)) (= "CLICK" (:Event body-json)) (= "V1001" (:EventKey body-json)))
     (do
       (wcar* (car/ping) (car/setex (:FromUserName body-json) 600 "请输入验证码"))
       (format text-receive (:FromUserName body-json) (:ToUserName body-json) (:CreateTime body-json) "请发送您的手机号"))
     )))
