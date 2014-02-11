(ns garden.css
  (:require [garden.core :refer [css]]
            [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px]]
            [garden.color :as color :refer [hsl rgb]]
            [garden.stylesheet :refer [at-media]]
  )
)

(def red (hsl 0 100 50))
(def yellow (hsl 58 100 50))

(defstyles screen
           [:body
            {:font-family "sans-serif"
             :font-size (px 16)
             :color "red"
             }]
           )