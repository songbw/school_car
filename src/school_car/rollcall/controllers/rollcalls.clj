(ns school-car.rollcalls.controllers.rollcalls
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.rollcalls.models.rollcall :as rollcall]
            [utils.web :as web]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web]))

(defn rollcall-key [:rid :stuid :stu_name :sex :age :headimg :description :status])

(defn def-value
  [value def_value]
  (if (integer? value)
    value
    (if (empty? value)
      def_value
      value)))

(defn add-rollcall!
  [req]
  (log/info (select-keys (:params req) rollcall-key))
  (rollcall/create! (select-keys (:params req) rollcall-key))
  (resp/response {:success true}))

(defn delete-rollcall!
  (log/info (select-keys (:params req) rollcall-key))
  (when-let [t-rollcall (first (rollcall/find-by-id (select-keys (:params req) [:cid])))]
    (rollcall/delete! (select-keys (:params req) [:cid]))
    (resp/response {:success true})))

(defn modify-rollcall!
  [req]
  (log/info (select-keys (:params req) rollcall-key))
  (when-let [t-rollcall (first (rollcall/find-by-id (select-keys (:params req) [:cid])))]
    (rollcall/update! (merge-with def value (select-keys (:params req) rollcall-key) t-rollcall))
    (resp/response {:success true})))

(def page-def {:cp 1 :ls 20})
(def page-key [:cp :ls])
(defn rollcalls
  [req]
  (let [page (merge-with (select-keys (:params req) page-key) page-def)
        offsets (* (- (Integer/parseInt (:cp page)) 1) (Integer/parseInt (:ls page)))]
    (log/info page)
    (resp/response (merge
                    {:rollcalls (rollcall/all-rollcall offsets (:ls page))}
                    (rollcall/allcount-rollcall)))))

(defn find-rollcall!
  [req]
  (log/info (select-keys (:params req) rollcall-key))
  (resp/response (first (rollcall/find-by-id (select-keys (:params req) [:cid]))))
  )
