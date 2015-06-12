(ns school-car.banjis.models.banji
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
   (insert banjis
           (values (assoc params :clid (uuid/gen-uuid)))
           )))

(defn update!
  [params]
  (log/info params)
  (transaction
   (update banjis
           (set-fields (dissoc params :clid))
           (where (select-keys params [:clid])))
))

(defn delete!
  [params]
  (delete banjis
          (where (update-in params [:clid] bigdec))))

(defn find-by-id
  [params]
  (select banjis
          (fields :clid :name :teacher_name :teacher_headimg :telephone :description :username :passwd :school_id)
          (where (update-in params [:clid] bigdec))))

(defn all-banji
  [o l]
  (select banjis
          (fields :clid :name :teacher_name :teacher_headimg :telephone :description :username :passwd :school_id)
          (order :id :DESC)
          (offset o)
          limit l))

(defn allcount-banji
  []
  (select banjis
          (aggregate (count :*) :allcount)
          ))
