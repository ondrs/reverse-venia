(ns reverse-venia.core
  (:gen-class)
  (:require [mount.core :as mount]
            [reverse-venia.server]))


(defn -main
  [& args]
  (-> (mount/with-args {:env :prod})
      mount/start))
