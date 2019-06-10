(ns reverse-venia.core
  (:gen-class)
  (:require [mount.core :as mount]
            [reverse-venia.states]))


(defn -main
  [& args]
  (mount/start))
