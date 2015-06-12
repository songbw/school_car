(ns school-car.route
  (:refer-clojure :exclude [resultset-seq])
  (:require [compojure.route :as route]
            [clojure.string :as str]
            [school-car.students.controllers.students :as students]
            [school-car.weixin.controllers.weixins :as weixins]
            [school-car.rollcall.controllers.rollcalls :as rollcalls])
  (:use [compojure.core]
        [cheshire.core]
        [ring.util.response]))

(defroutes school-routes
  (POST "/student" [] students/add-student!)
  (PUT "/student" [] students/modify-student!)
  (DELETE "/student" [] students/delete-student!)
  (GET "/weixin" [] weixins/check-signature))
