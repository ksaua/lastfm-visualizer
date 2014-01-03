(defproject lastfm-visualizer "0.1.0-SNAPSHOT"
  :description "Generates .png files from lastfm export data"
  :url "http://github.com/ksaua/lastfm-visualizer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/algo.generic "0.1.1"]
                 [org.clojure/tools.trace "0.7.5"]
                 [org.clojure/math.combinatorics "0.0.7"]
                 [org.clojure/data.json "0.2.3"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.6.0"]]}}
  :main lastfm-visualizer.core)
