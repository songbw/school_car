(ns school-car.route
  (:refer-clojure :exclude [resultset-seq])
  (:require [compojure.route :as route]
            [clojure.string :as str]
            [school-car.students.controllers.students :as students]
            )
  (:use [compojure.core]
        [cheshire.core]
        [ring.util.response]))

(defroutes school-routes
  (POST "/student" [] students/add-student!)
  (PUT "/student" [] students/modify-student!)
  (DELETE "/student" [] students/delete-student!))
