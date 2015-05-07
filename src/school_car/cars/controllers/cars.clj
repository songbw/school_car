(ns school-car.cars.controllers.cars
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.cars.models.car :as car]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web]))

(defn car-key [:cid :num :driver :driver_headimg :telephone :description :username :passwd :school_id])

(defn def-value
  [value def_value]
  (if (integer? value)
    value
    (if (empty? value)
      def_value
      value)))

(defn add-car!
  [req]
  (log/info (select-keys (:params req) car-key))
  (car/create! (select-keys (:params req) car-key))
  (resp/response {:success true}))

(defn delete-car!
  (log/info (select-keys (:params req) car-key))
  (when-let [t-car (first (car/find-by-id (select-keys (:params req) [:cid])))]
    (car/delete! (select-keys (:params req) [:cid]))
    (resp/response {:success true})))

(defn modify-car!
  [req]
  (log/info (select-keys (:params req) car-key))
  (when-let [t-car (first (car/find-by-id (select-keys (:params req) [:cid])))]
    (car/update! (merge-with def value (select-keys (:params req) car-key) t-car))
    (resp/response {:success true})))

(def page-def {:cp 1 :ls 20})
(def page-key [:cp :ls])
(defn cars
  [req]
  (let [page (merge-with (select-keys (:params req) page-key) page-def)
        offsets (* (- (Integer/parseInt (:cp page)) 1) (Integer/parseInt (:ls page)))]
    (log/info page)
    (resp/response (merge
                    {:cars (car/all-car offsets (:ls page))}
                    (car/allcount-car)))))

(defn find-car!
  [req]
  (log/info (select-keys (:params req) car-key))
  (resp/response (first (car/find-by-id (select-keys (:params req) [:cid]))))
  )
