(ns lastfm-visualizer.middleware)

(defn weighted-plays
  [[time plays]]
  {(- time 4) (/ plays 12.0)
   (- time 3) (/ plays 6.0)
   (- time 2) (/ plays 3.0)
   (- time 1) (/ plays 1.5)
      time    (/ plays 1.0)
   (+ time 1) (/ plays 1.5)
   (+ time 2) (/ plays 3.0)
   (+ time 3) (/ plays 6.0)
   (+ time 4) (/ plays 12.0)})

(defn weighted-play-seq
  [play-seq]
  (assoc play-seq
    :plays
    (apply merge-with + (map weighted-plays (seq (:plays play-seq))))))

(defn group-scrobbles-in-same-frame
  [scrobble dataseconds-per-videoframe]
  (let [current-time (:time scrobble)
        grouped-time (quot current-time dataseconds-per-videoframe)]
    (assoc scrobble :time grouped-time)))

(defn min-scrobbles
  [minimum play-seqs]
  (filter #(> (reduce + (vals (:plays %))) minimum) play-seqs))
