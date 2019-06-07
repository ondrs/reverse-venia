(ns reverse-venia.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as rm.json]
            [jumblerg.middleware.cors :as jm.cors]
            [compojure.core :refer [defroutes POST]]
            [compojure.route :as route]
            [reverse-venia.parser :as parser]
            [clojure.pprint :as pprint]
            [mount.core :as mount :refer [defstate]]))


(defn parse-handler
  [{:keys [body] :as request}]
  (try
    (let [result (parser/parse-graphql (get body :query ""))]
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


(def app
  (-> routes
      (wrap-dir-index)
      (jm.cors/wrap-cors identity)
      (rm.json/wrap-json-body {:keywords? true})
      (rm.json/wrap-json-response)))


(defstate server
  :start (jetty/run-jetty app {:port  3448
                               ;; join the main thread only in production
                               :join? (= (:env (mount/args))
                                         :prod)})
  :stop (.stop server))