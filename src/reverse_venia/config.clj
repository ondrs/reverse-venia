(ns reverse-venia.config
  (:require [environ.core :refer [env]]))


(defn str->int
  [s]
  (if (number? s)
    s
    (Integer/parseInt s)))


(def ^:private config-map
  {:default {:port (str->int (get env :port 80))}
   :dev     {:port 3448}})


(defn get-config
  [env]
  (get config-map env (:default config-map)))

