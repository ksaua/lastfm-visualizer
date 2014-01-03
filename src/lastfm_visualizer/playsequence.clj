(ns lastfm-visualizer.playsequence
  (:use [clojure.algo.generic.functor :only [fmap]]))

(defrecord PlaySequence [artist plays position])

(defn new-play-sequence
  ([artist plays] (new-play-sequence artist plays (list 0 0)))
  ([artist plays position] (PlaySequence. artist plays position)))


(defn find-playsequence-boundaries
  [play-seq]
  (let [biggest-radius (apply max 0 (vals (:plays play-seq)))]
    (->> (:position play-seq)
         (map #(+ (Math/abs %) biggest-radius)))))


(defn find-playsequences-boundaries
  [play-seqs]
  (->> (map find-playsequence-boundaries play-seqs)
       (apply map vector)
       (map #(apply max 0 %))))
       

(defn find-start-time
  [play-seqs]
  (->> (map #(keys (:plays %1)) play-seqs)
       (apply concat)
       (apply min)))

(defn find-end-time
  [play-seqs]
  (->> (map #(keys (:plays %1)) play-seqs)
       (apply concat)
       (apply max)))


(defn generate-play-seqs
  "Takes in scrobbles and returns list of playsequences"
  [scrobbles]
  (->> (group-by :artist scrobbles)
       (fmap #(frequencies (map :time %)))
       (seq)
       (map (fn [[artist plays]] (new-play-sequence artist plays)))))
