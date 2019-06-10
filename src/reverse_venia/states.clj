(ns reverse-venia.states
  (:require [reverse-venia.config :as config]
            [reverse-venia.server :as server]
            [mount.core :as mount :refer [defstate]]))


(defstate config
  :start (-> (mount/args) :env config/get-config))


(defstate server
  :start (server/start-server config)
  :stop (.stop server))


