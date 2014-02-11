#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.0.0"

(set-env!
  :dependencies '[['tailrecursion/boot.core   "2.0.0"]
                  [tailrecursion/boot.task "2.0.0"]
                  [tailrecursion/hoplon "5.1.1"]
                  [org.clojure/clojurescript "0.0-2156"]
                  [garden "1.1.5"]]
  :src-paths    #{"src"}
  :out-path     "public"
  :garden       '[{:stylesheet garden.css/screen
                   :compiler {:pretty-print? false}}])

(add-sync! (get-env :out-path) #{"assets"})

(require '[tailrecursion.hoplon.boot :refer :all]
         '[garden.compiler           :refer [compile-css]]
         '[clojure.java.io           :as    io]
         '[tailrecursion.boot.core   :as    boot]
         '[tailrecursion.boot.task   :refer :all]
         
(deftask development
         "Build hoplon-demo for development."
         []
         (comp (watch) (hoplon {:prerender false 
                                                :pretty-print true
                                                :prerender     false
                                                :optimizations :whitespace})))

(deftask production
         "Build hoplon-demo for production."
         []
         (hoplon {:optimizations :advanced}))


(deftask brepl
         "launch browser repl, default point browser to public/index.html"
         [& [index-file]]
         (comp (cljs/+ :browser)
               (cljs/+brepl (or index-file "public/index.html"))
               (repl/repl)))


(deftask cljs-play
         "cljs repl playground"
         []
         (set-env! :dependencies '[[org.clojure/clojurescript "0.0-2156"]
                                   [ring "1.2.1"]
                                   [compojure "1.1.6"]
                                   [hiccup "1.0.5"]
                                   [com.cemerick/austin "0.1.3"
                                    :exclusions [org.clojure/clojurescript]]])
         (require 'cemerick.piggieback)
         (comp (cljs :output-to   "public/app.js")
               (pmbauer/repl
                 :init-ns 'cljs-repl
                 :middlewares [#'cemerick.piggieback/wrap-cljs-repl]))))

(deftask dev
  "Build hoplon.io for local development."
  []
  (comp (watch) (hoplon {:pretty-print  true
                         :prerender     false
                         :optimizations :whitespace})))

(deftask prod
  "Build hoplon.io for production deployment."
  []
  (hoplon {:optimizations :advanced}))

(defn garden-compile
  [{:keys [stylesheet compiler] :as build}]
  (let [tmpdir   (boot/mkoutdir! ::garden-tmp)
        out-file (->> (or (get compiler :output-to)
                          (str (name stylesheet) ".css"))
                      (io/file tmpdir)
                      .getPath)]
    (println "[garden] Compiling" stylesheet "...")
    (require (symbol (namespace stylesheet)) :reload-all)
    (compile-css
      (assoc compiler :output-to out-file)
      @(resolve stylesheet))))

(deftask garden [& opts]
         (fn [continue]
           (fn [event]
             (when-let [builds (get-env :garden)]
               (doseq [build builds] (garden-compile build)))
             (continue event))))


(deftask development
         "Build hoplon-demo for development."
         []
         (comp (watch) (hoplon {:prerender false :pretty-print true})))

(deftask production
         "Build hoplon-demo for production."
         []
         (hoplon {:optimizations :advanced}))


(deftask brepl
         "launch browser repl, default point browser to public/index.html"
         [& [index-file]]
         (comp (cljs/+ :browser)
               (cljs/+brepl (or index-file "public/index.html"))
               (repl/repl)))


(deftask cljs-play
         "cljs repl playground"
         []
         (set-env! :dependencies '[[org.clojure/clojurescript "0.0-2156"]
                                   [ring "1.2.1"]
                                   [compojure "1.1.6"]
                                   [hiccup "1.0.5"]
                                   [com.cemerick/austin "0.1.3"
                                    :exclusions [org.clojure/clojurescript]]])
         (require 'cemerick.piggieback)
         (comp (cljs :output-to   "public/app.js")
               (pmbauer/repl
                 :init-ns 'cljs-repl
                 :middlewares [#'cemerick.piggieback/wrap-cljs-repl])))