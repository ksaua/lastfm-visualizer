(ns lastfm-visualizer.core
  (:require [lastfm-visualizer.playsequence :refer [generate-play-seqs]])
  (:require [lastfm-visualizer.position-algorithm.bruteforce-circle :refer [place-play-seqs]])
  (:require [lastfm-visualizer.json-scrobble-parser :refer [parse-json-file]])
  (:require [lastfm-visualizer.middleware :refer [weighted-play-seq
                                                  group-scrobbles-in-same-frame
                                                  min-scrobbles]])
  (:require [lastfm-visualizer.png-renderer :refer [render]])
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
        (place-play-seqs)
        ((fn [seqs] (do (println seqs) seqs)))
        ((fn [seqs] (render "/home/knut/.viz" 800 600 seqs 1))))))


(defn -main
  [& filepaths]
  (visualize-json-files filepaths))
