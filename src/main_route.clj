(ns main-route
  (:refer-clojure :exclude [resultset-seq])
  (:require [compojure.route :as route])
  (:use [compojure.core]
        [cheshire.core]
        [ring.util.response])
  (:use [school-car.route])
  )

(defroutes core-routes
  (GET "/" [] (response {:hello "It works!"}))
  (context "/pieces" [] school-routes)
  (route/not-found "Not Found"))
