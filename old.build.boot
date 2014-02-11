#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.0.0"

(set-env!
  :dependencies  '[[tailrecursion/boot.core   "2.0.0"]
                  [tailrecursion/boot.task   "2.0.0"]
                  [tailrecursion/hoplon      "5.0.0"]
                  [markdown-clj              "0.9.38"]
                  [org.clojure/clojurescript "0.0-2156"]
                  [garden "1.1.5"]]
  :src-paths     #{"src"}
  :out-path      "public"
  :garden       '[{:stylesheet garden.css/screen
                   :compiler {:pretty-print? false}}]
  )

(add-sync! (get-env :out-path) #{"assets"})

(require
  '[tailrecursion.boot.task   :refer :all]
  '[tailrecursion.hoplon.boot :refer :all]
  '[tailrecursion.boot.core   :as    boot]
  '[garden.compiler           :refer [compile-css]]
  '[clojure.java.io           :as    io]
  )


(deftask dev
         "Build & watch for local dev"
         []
         (comp (watch) (garden) (hoplon {:pretty-print  true
                                :prerender     false
                                :optimizations :whitespace})))

(deftask single
         "Build single for local dev."
         []
         (hoplon {:pretty-print  true
                  :prerender     false
                  :optimizations :whitespace}))

(deftask prod
         "Build for production deployment."
         []
         (hoplon {:optimizations :advanced}))

(def html-out "resources/html")

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

 (defn garden-build [& opts]
   (fn [continue]
     (fn [event]
       (when-let [builds (get-env :garden)]
         (doseq [build builds] (garden-compile build)))
       (continue event))))

 (deftask garden
          []
          (set-env! :garden '[{:stylesheet garden.css/screen
                               :compiler   {:pretty-print? false}}])
          (add-sync! (get-env :out-path) [html-out])
          (garden-build))

 (deftask hoplon
          [& [opts]]
          (set-env! :out-path html-out)
          (require '[tailrecursion.hoplon.boot :as h])
          (apply h/hoplon [opts]))