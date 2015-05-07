(ns school-car.cars.models.car
  (:require [clojure.java.jdbc :as sql]
            [utils.log :as log]
            [utils.uuid :as uuid]
            school-car.config.database)
  (:use [korma.core]
        [korma.db]
        [school-car.entity.entities]))

(defn create!
  [params]
  (transaction
   (insert cars
           (values (assoc params :cid (uuid/gen-uuid))))))

(defn update!
  [params]
  (log/info params)
  (transaction cars
               (set-fields (dissoc params :cid))
               (where (select-keys params [:cid]))))

(defn delete!
  [params]
  (delete cars
          (where (update-in params [:caid] bigdec))))

(defn find-by-id
  [params]
  (select cars
          (fields :cid :num :driver :driver_headimg :telephone :description :username :passwd :school_id)
          (where (update-in params [:cid] bigdec))))

(defn all-car
  [o l]
  (select cars
          (fields :cid :num :driver :driver_headimg :telephone :description :username :passwd :school_id)
          (order :id :DESC)
          (offset o)
          limit l))

(defn allcount-car
  []
  (select cars
          (aggregate (count :*) :allcount)
          ))
