(ns utils.log
  (:require [clojure.tools [logging :as log]]))

(defmacro info [& args]
  `(log/info (str ~@args)))

(defmacro error [e & args]
  `(log/log :error ~e (str ~@args)))

(defmacro debug [& args]
  `(log/debug (str ~@args)))

(defmacro warn-error [e & args]
  `(log/warn (str ~@args) ~e))

(defmacro warn [& args]
  `(log/warn (str ~@args)))

(defn log-capture! [& args]
  (apply log/log-capture! args))

(defn log-stream [& args]
  (apply log/log-stream args))
