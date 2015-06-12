(ns server.server
  (:require [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [utils.web :as web]
            [utils.log :as log]
            [utils.common :as common]
;;            [pieces.stocks.service.stock :as ser-stock]
            )
  (:use main-route)
  (:use [ring.middleware params
         keyword-params])
  (:gen-class))

(def app
  (-> core-routes
      wrap-keyword-params
      wrap-params
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (middleware/wrap-json-params)
      (web/log-request-response)
      (web/wrap-request-header)
      ))

(defn start [port]
  (jetty/run-jetty #'app {:port (or port 6001) :join? false}))

(defn -main []
  (if-let [fname (common/getParam "file_name" nil)]
    (do
;;       (ser-stock/import-pieces fname)
       (log/info "import data success, please modify config file and restart system")
       (System/exit 0))
    (let [port (Integer/parseInt (get (System/getenv) "PORT" "6001"))]
      (log/info "POST IS " (get (System/getenv) "PORT"))
      (start port)))
  )
