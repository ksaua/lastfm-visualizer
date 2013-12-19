(ns lastfm-visualizer.playsequence
  (:use [clojure.algo.generic.functor :only [fmap]]))

(defrecord PlaySequence [artist plays position])

(defn generate-play-seqs
  "Takes in scrobbles and returns list of playsequences"
  [scrobbles]
  (->> (group-by :artist scrobbles)
       (fmap #(frequencies (map :time %)))
       (seq)
       (map (fn [[artist plays]] (PlaySequence. artist plays (list 0 0))))))
