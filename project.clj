(defproject reverse-venia "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.4.474"]
                 [graphql-clj "0.2.6"]
                 [mount "0.1.16"]
                 [environ "1.1.0"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [jumblerg/ring-cors "2.0.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [compojure "1.6.1"]
                 [cljs-http "0.1.46"]]

  :plugins [[lein-figwheel "0.5.16"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :main ^:skip-aot reverse-venia.core

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src"]

                ;; The presence of a :figwheel configuration here
                ;; will cause figwheel to inject the figwheel client
                ;; into your build
                :figwheel     {:on-jsload "reverse-venia.client/on-js-reload"
                               ;; :open-urls will pop open your application
                               ;; in the default browser once Figwheel has
                               ;; started and compiled your application.
                               ;; Comment this out once it no longer serves you.
                               :open-urls ["http://localhost:3449/index.html"]}

                :compiler     {:main                 reverse-venia.client
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/reverse_venia.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :source-map-timestamp true
                               ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                               ;; https://github.com/binaryage/cljs-devtools
                               :preloads             [devtools.preload]}}
               ;; This next build is a compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to       "resources/public/js/compiled/reverse_venia.js"
                               :closure-defines {reverse-venia.client/base-url ""}
                               :main            reverse-venia.client
                               :optimizations   :advanced
                               :pretty-print    false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"]             ;; watch and update CSS
             }


  ;; Setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:dev        {:dependencies  [[binaryage/devtools "0.9.9"]
                                          [figwheel-sidecar "0.5.16"]
                                          [org.clojure/tools.namespace "0.3.0"]]
                          ;; need to add dev source path here to get user.clj loaded
                          :source-paths  ["src" "dev" "test"]
                          ;; need to add the compliled assets to the :clean-targets
                          :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                            :target-path]}

             :uberjar    {:aot        :all
                          :prep-tasks ["compile" ["cljsbuild" "once" "min"]]}})
