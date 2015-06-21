(ns server.server
  (:require [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [imintel.ring.xml :as imintel]
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
      ;; (imintel/wrap-xml-request)
      ;; (imintel/wrap-xml-response)
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
    (let [port (Integer/parseInt (System/getProperty "PORT"))]
      (log/info "POST IS " port)
      (start port)))
  )
