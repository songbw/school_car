(ns school-car.weixin.controllers.weixins
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.weixin.models.weixin :as weixin]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web])
  (:import java.security.MessageDigest))

(def sign-key [:signature :timestamp :nonce :echostr])

(defn check-signature
  [req]
  (log/info (:params req))
  (log/info (select-keys (:params req) sign-key))
  (resp/response (select-keys (:params req) [:echostr]))
  )
