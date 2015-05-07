(ns school-car.students.models.student
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
   (insert students
           (values (assoc params :stuid (uuid/gen-uuid))))))

(defn update!
  [student]
  (log/info student)
  (transaction
   (update students
           (set-fields (dissoc student :stuid))
           (where (select-keys student [:stuid])))))

(defn delete!
  [stuid]
  (delete students
          (where (update-in stuid [:stuid] bigdec) )))

(defn find-by-car
  [car_id]
  (select students
          (fields :stuid :name :sex :age :classes_id :car_id :school_id :username :passwd :description :headimg)
          (where {:car_id car_id})))

(defn find-by-id
  [id]
  (select students
          (fields :stuid :name :sex :age :classes_id :car_id :school_id :username :passwd :description :headimg)
          (where (update-in id [:stuid] bigdec))))

(defn all-school
  [school o l]
  (select students
          (fields :stuid :name :sex :age :classes_id :car_id :school_id :username :passwd :description :headimg)
          (order :id :DESC)
          (offset o)
          (limit l)))

(defn allcount-school
  [school]
  (select students
          (aggregate (count :*) :allcount)
          (where {:school_id school})))
