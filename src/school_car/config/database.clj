(ns school-car.config.database
  (:require [clojure.string :as string])
  (:use [korma.db])
  (:use [utils.common]))

(def db-pg (postgres {:db (getParam "db_name" "school_car")
                      :user (getParam "db_user" "postgres")
                      :password (getParam "db_password" "postgres")
                      :host (getParam "db_host" "127.0.0.1")
                      :port (getParam "db_port" "5432")
                      :delimiters ""
                      :nameing {:keys string/lower-case
                                :fields string/upper-case}}))

(defdb db db-pg)
