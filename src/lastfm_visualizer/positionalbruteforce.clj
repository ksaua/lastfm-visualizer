(ns lastfm-visualizer.positionalbruteforce
  (:use [clojure.set :only [intersection]])
  (:use [clojure.math.combinatorics :only [combinations]]))

(defn hashmap-combo-key
  [playseq1 playseq2]
  (let [artist1 (:artist playseq1)
        artist2 (:artist playseq2)]
    (if (= (compare artist1 artist2 ) 1)
      (list artist1 artist2)
      (list artist2 artist1))))

(defn min-distance 
  "Takes two play sequences finding the minimum amount of distance needed between them over time for them not to overlap"
  [playseq1 playseq2]
  (let [plays1 (:plays playseq1)
        plays2 (:plays playseq2)
        timeslots1 (set (keys plays1))
        timeslots2 (set (keys plays2))]
    (->>
     (intersection timeslots1 timeslots2) ; Find common time slots
     (map #(+ (get plays1 %1) (get plays2 %1)))     ; Calculate min distance within a timeslot 
     (apply max 0))))


(defn combo-distances
  [play-seqs]
  (->> (combinations (range (count play-seqs)) 2) ; Find combination of indices
       (map 
        (fn [[index-a index-b]]
          [(hashmap-combo-key (nth play-seqs index-a) (nth play-seqs index-b))
           (min-distance (nth play-seqs index-a) (nth play-seqs index-b))]))
       (into {})))





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

(def positions
  (reductions
   (fn [[x y] [dx dy]] [(+ x dx) (+ y dy)])
   (dPos-seq)))


(defn circle-collides?
  [x1 y1 x2 y2 maks-collision-distance]
  (> (Math/pow maks-collision-distance 2)
     (+ 
      (Math/pow (- x1 x2) 2)
      (Math/pow (- y1 y2) 2))))

(defn pos-available?
  [pos play-seq other-seqs combo-distances]
  (letfn [(collides? [other-seq]
            (let [[x1 y1] pos
                  [x2 y2] (:position other-seq)
                  key (hashmap-combo-key play-seq other-seq)
                  collision-distance (get combo-distances key)]
              (if (nil? collision-distance)
                        false
                        (circle-collides? x1 y1 x2 y2 collision-distance))))]
     (every? (comp not collides?) other-seqs)))

(defn find-first
  "Returns the first element in seq which (func el) returns true"
  [func seq]
  (first (drop-while (comp not func) seq)))

(defn find-position
  [play-seq other-seqs combo-distances]
  (find-first
   #(pos-available? % play-seq other-seqs combo-distances)
   positions))

(defn bruteforce-place-play-seqs
  [play-seqs combo-distances]
  (loop [placed-seqs []
         not-placed-seqs play-seqs]
    (let [current-seq (first not-placed-seqs)
          new-position (find-position current-seq placed-seqs combo-distances)]
      (if (empty? not-placed-seqs)
        placed-seqs
        (recur (conj placed-seqs
                     (assoc current-seq :position new-position))
               (rest not-placed-seqs))))))
