(ns school-car.students.controllers.students
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.students.models.student :as student]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web]))

(def student-key [:name :sex :age :classes_id :car_id :school_id :username :passwd :description :headimg])

(defn def-value
  [value def_value]
  (if (integer? value)
    value
    (if (empty? value)
      def_value
      value)))

(defn add-student!
  [req]
  (log/info (select-keys (:params req) student-key))
  (student/create! (select-keys (:params req) student-key))
  (resp/response {:success true})
  )

(defn delete-student!
  [req]
  (log/info (select-keys (:params req) student-key))
  (when-let [m-student (first (student/find-by-id (select-keys (:params req) [:stuid])))]
    (student/delete! (select-keys (:params req) [:stuid]))
    (resp/response {:success true}))
)

(defn modify-student!
  [req]
  (log/info (select-keys (:params req) student-key))
  (when-let [m-student (first (student/find-by-id (select-keys (:params req) [:stuid])))]
    (student/update! (merge-with def-value (select-keys (:params req) student-key) m-student))
    (resp/response {:success true})
    ))

(def page-def {:cp 1 :ls 20})
(def page-key [:cp :ls])
(defn students-school
  [req]
  (let [page (merge-with (select-keys (:params req) page-key) page-def)
        offsets (* (- (Integer/parseInt (:cp page)) 1) (Integer/parseInt (:ls page)))]
    (log/info page)
    (resp/response (merge
                    {:students (student/all-school (:school (:params req)) offsets (:ls page))}
                    (student/allcount-school (:school (:params req)))))))

(defn students-car
  [req]
  (resp/response {:students (student/find-by-car (:car-id (:params req)))}))
