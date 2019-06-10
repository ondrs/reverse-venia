(ns reverse-venia.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as rm.json]
            [jumblerg.middleware.cors :as jm.cors]
            [compojure.core :refer [defroutes POST]]
            [compojure.route :as route]
            [reverse-venia.graphql-parser :as graphql-parser]
            [clojure.pprint :as pprint]
            [mount.core :as mount]))


(defn parse-handler
  [{:keys [body] :as request}]
  (try
    (let [result (graphql-parser/parse-graphql (get body :query ""))]
      {:status 200
       :body   {:raw       result
                :formatted (-> result pprint/pprint with-out-str)}})
    (catch Exception e
      {:status 500
       :body   {:error (.getMessage e)}})))


(defroutes routes
  (POST "/parse" request (parse-handler request))
  (route/resources "/")
  (route/not-found "Page not found"))


(defn wrap-dir-index
  [handler]
  (fn [request]
    (handler
      (update-in request [:uri]
                 #(if (= "/" %) "/index.html" %)))))


(def handler
  (-> routes
      (wrap-dir-index)
      (jm.cors/wrap-cors identity)
      (rm.json/wrap-json-body {:keywords? true})
      (rm.json/wrap-json-response)))


(defn start-server
  [config]
  (jetty/run-jetty handler {:port  (:port config)
                            ;; join the main thread only in production
                            :join? (not= (:env (mount/args))
                                         :dev)}))
