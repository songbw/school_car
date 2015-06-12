(ns school-car.rollcall.controllers.rollcalls
  (:require [clojure.string :as str]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            )
  (:require [school-car.rollcall.models.rollcall :as rollcall]
            [utils.common :as common]
            [utils.log :as log])
  (:use [compojure.core]
        [utils.web]))
