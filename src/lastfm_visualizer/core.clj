(ns lastfm-visualizer.core
  (:use [lastfm-visualizer.playsequence :only [generate-play-seqs]])
  (:use [lastfm-visualizer.position-algorithm.bruteforce-circle :only [place-play-seqs]])
  (:use [lastfm-visualizer.json-scrobble-parser :only [parse-json-file]])
  (:use [lastfm-visualizer.middleware :only [weighted-play-seq
                                             group-scrobbles-in-same-frame
                                             min-scrobbles]])
  (:use [clojure.algo.generic.functor :only [fmap]])
  (:gen-class))

(def video-fps 30)
(def data-months-per-video-second 1)
(def data-seconds-per-video-frame
  (quot (* data-months-per-video-second 30 24 60 60) video-fps))


(defn visualize-json-files
  [files]
  (println 
   (->> (map parse-json-file files)
        (apply concat)
        (map #(group-scrobbles-in-same-frame % data-seconds-per-video-frame))
        (generate-play-seqs)
        (min-scrobbles 10)
        (map weighted-play-seq)
        (place-play-seqs))))

(defn -main
  [& filepaths]
  (visualize-json-files filepaths))
