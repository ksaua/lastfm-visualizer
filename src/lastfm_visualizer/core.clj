(ns lastfm-visualizer.core
  (:require [clojure.algo.generic.functor :as functor])
  (:require [clojure.set :as set])
  (:require [clojure.math.combinatorics :as combo]))

(defn if-nil [a b]
  (if (nil? a)
    b
    a))

(defrecord Scrobble [time artist])

(def scrobbles 
  (list (Scrobble. 1 "Artist1")
        (Scrobble. 2 "Artist2")
        (Scrobble. 3 "Artist1")
        (Scrobble. 4 "Artist1")
        (Scrobble. 4 "Artist1") 
        (Scrobble. 5 "Artist2")))


(defn group-plays
  "Takes in scrobbles and returns a list like this:
   {:Artist1 [1 3 4] :Artist2 [2 5]}"
  [scrobbles]
  (loop [scrobbles scrobbles
         hash-map {}]
    (if (empty? scrobbles)
      hash-map
      (recur (rest scrobbles)
             (let [scrobble (first scrobbles)
                   artist (keyword (:artist scrobble))
                   time (:time scrobble)
                   ; Set current-squence to empty if none exists
                   current-sequence (if-nil (artist hash-map) [])]
               (assoc hash-map artist 
                      (conj current-sequence time)))))))

(defn count-similar
  "Takes a list [1 2 2 3 4 4 4] and
   returns a hash-map like {1 1, 2 2, 3 1, 4 3}"
  [lst]
  (loop [lst lst
         hash-map {}]
    (if (empty? lst)
      hash-map
      (recur (rest lst)
             (let [key (first lst)
                   prev-value (if-nil (get hash-map key) 0)]
               (assoc hash-map key (inc prev-value)))))))

(defn count-groups
  "Takes in grouped scrobbles and returns a list like:
   {:Artist1 {1 1, 3 1, 4 2} :Artist2 {2 1, 5 1}}"
  [group-map]
  (functor/fmap count-similar group-map))


(defn min-distance 
  "Takes two hash-maps {1 1, 3 1, 4 2} and {1 1, 5 1}, interpreting the key as a timeslot and the value as the radius for a circle, and finding the minimum amount of distance needed between circles over time for them not to overlap"
  [r1s r2s]
  (let [timeslots1 (set (keys r1s))
        timeslots2 (set (keys r2s))
        ; Find common time slots
        common-timeslots (set/intersection timeslots1 timeslots2)

        ; Calculate min distance within a timeslot 
        distances (map #(+ (get r1s %1) (get r2s %1)) common-timeslots)]
    (apply max 0 distances)))

(defn generate-play-sequence
  [scrobbles]
  (-> scrobbles 
      group-plays
      count-groups))
