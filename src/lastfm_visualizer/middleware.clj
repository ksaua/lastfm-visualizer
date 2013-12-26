(ns lastfm-visualizer.middleware)

(defn weighted-plays
  [[time plays]]
  {(- time 4) (/ plays 12)
   (- time 3) (/ plays 6)
   (- time 2) (/ plays 3)
   (- time 1) (/ plays 3/2)
      time       plays
   (+ time 1) (/ plays 3/2)
   (+ time 2) (/ plays 3)
   (+ time 3) (/ plays 6)
   (+ time 4) (/ plays 12)})

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
