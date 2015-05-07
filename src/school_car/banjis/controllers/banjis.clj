(ns school-car.banjis.controllers.banjis
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.banjis.models.banji :as banji]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web]))

(defn banji-key [:clid :name :teacher_name :teacher_headimg :telephone :description :username :passwd :school_id])

(defn def-value
  [value def_value]
  (if (integer? value)
    value
    (if (empty? value)
      def_value
      value)))

(defn add-banji!
  [req]
  (log/info (select-keys (:params req) banji-key))
  (banji/create! (select-keys (:params req) banji-key))
  (resp/response {:success true}))

(defn delete-banji!
  (log/info (select-keys (:params req) banji-key))
  (when-let [t-banji (first (banji/find-by-id (select-keys (:params req) [:cid])))]
    (banji/delete! (select-keys (:params req) [:cid]))
    (resp/response {:success true})))

(defn modify-banji!
  [req]
  (log/info (select-keys (:params req) banji-key))
  (when-let [t-banji (first (banji/find-by-id (select-keys (:params req) [:cid])))]
    (banji/update! (merge-with def value (select-keys (:params req) banji-key) t-banji))
    (resp/response {:success true})))

(def page-def {:cp 1 :ls 20})
(def page-key [:cp :ls])
(defn banjis
  [req]
  (let [page (merge-with (select-keys (:params req) page-key) page-def)
        offsets (* (- (Integer/parseInt (:cp page)) 1) (Integer/parseInt (:ls page)))]
    (log/info page)
    (resp/response (merge
                    {:banjis (banji/all-banji offsets (:ls page))}
                    (banji/allcount-banji)))))

(defn find-banji!
  [req]
  (log/info (select-keys (:params req) banji-key))
  (resp/response (first (banji/find-by-id (select-keys (:params req) [:cid]))))
  )
