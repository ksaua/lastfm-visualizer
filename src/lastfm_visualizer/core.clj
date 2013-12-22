(ns lastfm-visualizer.core
  (:use [lastfm-visualizer.playsequence :only [generate-play-seqs]])
  (:use [lastfm-visualizer.position-algorithm.bruteforce-circle :only [place-play-seqs]])
  (:use [lastfm-visualizer.json-scrobble-parser :only [parse-json-file]])
  (:gen-class))


(defn visualize-json-files
  [files]
  (println 
   (->> (map parse-json-file files)
        (apply concat)
        (generate-play-seqs)
        (place-play-seqs))))

(defn -main
  [& filepaths]
  (visualize-json-files filepaths))
