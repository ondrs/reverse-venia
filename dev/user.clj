(ns user
  (:require [figwheel-sidecar.repl-api :as f]
            [clojure.tools.namespace.repl :refer [refresh]]
            [mount.core :as mount :refer [defstate]]))


;; ==============================================================================
;; Figwheel
;; ==============================================================================


(defn fig-start
  "This starts the figwheel server and watch based auto-compiler."
  []
  ;; this call will only work are long as your :cljsbuild and
  ;; :figwheel configurations are at the top level of your project.clj
  ;; and are not spread across different lein profiles

  ;; otherwise you can pass a configuration into start-figwheel! manually
  (f/start-figwheel!))


(defn fig-stop
  "Stop the figwheel server and watch based auto-compiler."
  []
  (f/stop-figwheel!))


(defstate figwheel
  :start (fig-start)
  :stop (fig-stop))


;; ==============================================================================
;; Reloaded
;; ==============================================================================

(defn start []
  (-> (mount/with-args {:env :dev})
      mount/start))


(defn stop []
  (mount/stop))


(defn go []
  (start)
  :ready)


(defn reset []
  (stop)
  (refresh :after 'user/go))


;; ==============================================================================
;; Dev
;; ==============================================================================


(comment
  (go)
  (reset)
  )
