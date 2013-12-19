(ns lastfm-visualizer.core
  (:use [lastfm-visualizer.playsequence :only [generate-play-seqs]])
  (:use [lastfm-visualizer.position-algorithm.bruteforce-circle :only [place-play-seqs]]))

(defrecord Scrobble [time artist])
(defn new-scrobble
  [time artist]
  (Scrobble. time artist))

(def scrobbles 
  (list (Scrobble. 1 "Artist1")
        (Scrobble. 2 "Artist2")
        (Scrobble. 2 "Artist4")
        (Scrobble. 3 "Artist1")
        (Scrobble. 4 "Artist1")
        (Scrobble. 4 "Artist1") 
        (Scrobble. 5 "Artist2")
        (Scrobble. 5 "Artist1")
        (Scrobble. 5 "Artist1")
        (Scrobble. 5 "Artist3")
        (Scrobble. 6 "Artist3")
        (Scrobble. 6 "Artist3")
        (Scrobble. 7 "Artist4")
        (Scrobble. 5 "Artist4")
        (Scrobble. 7 "Artist5")
        (Scrobble. 7 "Artist2")
        (Scrobble. 7 "Artist3")
        (Scrobble. 10 "Artist4")
        (Scrobble. 11 "Artist4")
        (Scrobble. 13 "Artist4")
        (Scrobble. 14 "Artist4")
        (Scrobble. 15 "Artist4")
        (Scrobble. 23 "Artist4")))

(defn main
  []
  (let [play-seqs (generate-play-seqs scrobbles)]
    (place-play-seqs play-seqs)))
