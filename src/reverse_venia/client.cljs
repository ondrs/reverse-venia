(ns reverse-venia.client
  (:require [cljs-http.client :as http]
            [cljs.core.async :as async]
            [clojure.string :as string]
            [goog.events :as events]
            [goog.dom :as dom]))


(enable-console-print!)

(goog-define base-url "http://localhost:3448")

(defonce app-state
  (atom {:history []}))


(defn print-history
  []
  (let [history-el (dom/getElement "history")
        html       (->> (:history @app-state)
                        reverse
                        (take 20)
                        (map (fn [{:keys [uuid query]}]
                               (str "<a href=\"#\" id=\"" uuid "\" class=\"history-link\">"
                                    (subs query 0 100)
                                    "...</a>")))
                        string/join)]
    (set!
      (.-innerHTML history-el)
      html)))



(defn post-parse-request
  [query]
  (http/post (str base-url "/parse")
             {:json-params {:query query}}))


(declare on-js-reload)


(defn on-submit
  [e]
  (.preventDefault e)
  (async/go
    (let [result-el     (dom/getElement "result")
          query-el      (dom/getElement "query")
          query         (.-value query-el)
          response      (async/<! (post-parse-request query))
          response-body (:body response)]

      ;; Update History
      (swap! app-state update :history conj {:uuid     (random-uuid)
                                             :query    query
                                             :response response-body})

      (if (= (:status response) 200)
        (do
          (set!
            (.-innerHTML result-el)
            (:formatted response-body)))
        (do
          (set!
            (.-innerHTML result-el)
            (:error response-body))))
      ;; refresh
      (on-js-reload))))


(defn on-history-link-click
  [e]
  (.preventDefault e)
  (let [id     (-> e .-target .-id)
        record (->> (:history @app-state)
                    (filter #(= (-> % :uuid str) id))
                    first)]
    (set!
      (.-innerHTML (dom/getElement "result"))
      (or
        (-> record :response :error)
        (-> record :response :formatted)))

    (set!
      (.-value (dom/getElement "query"))
      (:query record))))


(defn register-listeners
  []
  (doto (dom/getElement "form")
    events/removeAll
    (events/listen "submit" on-submit))

  (doseq [el (array-seq (dom/getElementsByClass "history-link"))]
    (doto el
      events/removeAll
      (events/listen "click" on-history-link-click))))


(defonce init
  (register-listeners))


(defn on-js-reload
  []
  (print-history)
  (register-listeners))
