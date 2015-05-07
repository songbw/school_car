(ns school-car.entity.entities
  (:use [korma.core]))

(declare students)

(defentity students
  (table :students))

(defentity cars
  (table :cars))

(defentity banjis
  (table :classes))

(defentity rollcalls
  (table :rollcalls))
