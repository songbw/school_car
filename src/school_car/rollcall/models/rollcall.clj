(ns school-car.rollcall.models.rollcall
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
   (insert rollcalls
           (values (assoc params :rid (uuid/gen-uuid))))))

(defn update!
  [params]
  (log/info params)
  (transaction
   (update rollcalls
           (set-fields (dissoc params :rid))
           (where (select-keys params [:rid])))
))

(defn delete!
  [params]
  (delete rollcalls
          (where (update-in params [:rid] bigdec))))

(defn find-by-id
  [params]
  (select rollcalls
          (fields :rid :stuid :stu_name :sex :age :headimg :description :status)
          (where (update-in params [:rid] bigdec))))

(defn all-rollcall
  [o l]
  (select rollcalls
          (fields :rid :stuid :stu_name :sex :age :headimg :description :status)
          (order :id :DESC)
          (offset o)
          limit l))

(defn allcount-rollcall
  []
  (select rollcalls
          (aggregate (count :*) :allcount)
          ))
