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
        (Scrobble. 7 "Artist4")))


(defn generate-play-seqs
  "Takes in scrobbles and returns a map like this:
   {Artist1 {1 1, 3 1, 4 1}, Artist2 {2 1, 5 1}}"
  [scrobbles]
  (->> (group-by :artist scrobbles)
       (functor/fmap #(frequencies (map :time %)))))


(defn min-distance 
  "Takes two hash-maps {1 1, 3 1, 4 2} and {1 1, 5 1}, interpreting the key as a timeslot and the value as the radius for a circle, and finding the minimum amount of distance needed between circles over time for them not to overlap"
  [r1s r2s]
  (let [timeslots1 (set (keys r1s))
        timeslots2 (set (keys r2s))]
    (->>
     (set/intersection timeslots1 timeslots2) ; Find common time slots
     (map #(+ (get r1s %1) (get r2s %1)))     ; Calculate min distance within a timeslot 
     (apply max 0))))


(defn hashmap-combo-key
  [string1 string2]
  (if (= (compare string1 string2) 1)
    (list string1 string2)
    (list string2 string1)))


(defn combo-distances
  [play-seqs]
  (->> (combo/combinations (keys play-seqs) 2)
       (map 
        (fn [[a b]]
          [(hashmap-combo-key a b)
           (min-distance (get play-seqs a) (get play-seqs b))]))
       (into {})))



(def positions
  (reductions
   (fn [[x y] [dx dy]] [(+ x dx) (+ y dy)])
   (dPos-seq)))

(defn dPos
  [n]
  (if (= n 0)
    (list [0 0])
    (let [x (int (Math/pow -1 (dec n)))]
      (concat (repeat n [x 0]) (repeat n [0 x])))))

(defn dPos-seq
  "Lazy sequence for dPos"
  ([] (dPos-seq 0))
  ([n] (lazy-cat (dPos n) (dPos-seq (inc n)))))

(defn circle-collides?
  [x1 y1 x2 y2 maks-collision-distance]
  (> (Math/pow maks-collision-distance 2)
     (+ 
      (Math/pow (- x1 x2) 2)
      (Math/pow (- y1 y2) 2))))

(defn pos-available?
  [pos play-seq other-seqs combo-distances]
  (letfn [(collides? [other-seq-key]
            (let [other-seq (get other-seqs other-seq-key)
                  [x1 y1] pos
                  [x2 y2] (:position other-seq)
                  key (hashmap-combo-key
                       (first (keys play-seq))
                       other-seq-key)
                  collision-distance (get combo-distances key)]
              (circle-collides? x1 y1 x2 y2 collision-distance)))]
    (every? (comp not collides?) (keys other-seqs))))

(defn find-first
  "Returns the first element in seq which (func el) returns true"
  [func seq]
  (first (drop-while (comp not func) seq)))

(defn find-position
  [play-seq other-seqs combo-distances]
  (find-first
   #(pos-available? % play-seq other-seqs combo-distances)
   positions))

(defn place-play-seqs
  [play-seqs combo-distances]
  (loop [placed-seqs []
         not-placed-seqs play-seqs]
    (let [current-key (first (keys not-placed-seqs))
          current-seq (get not-placed-seqs current-key)
          new-position (find-position current-seq placed-seqs combo-distances)]
      (if (empty? not-placed-seqs)
        placed-seqs
        (recur (conj placed-seqs
                     (assoc current-seq :position new-position))
               (dissoc not-placed-seqs current-key))))))


(defn main
  []
  (let [play-seqs (generate-play-seqs scrobbles)
        combo-distances (combo-distances play-seqs)]
    (place-play-seqs play-seqs combo-distances)))
