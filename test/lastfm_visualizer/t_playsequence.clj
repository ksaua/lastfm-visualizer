(ns lastfm-visualizer.t-playsequence
  (:use midje.sweet)
  (:require [lastfm-visualizer.playsequence :as ps]))

(def empty-play-seq (ps/new-play-sequence "Test" []))
(def many-play-seqs
  [(ps/new-play-sequence "Bonobo" {50 2 51 3 52 4 72 4} (list 0 0))
   (ps/new-play-sequence "Emancipator" {19 3 26 3 22 0 21 3} (list 10 0))
   (ps/new-play-sequence "Little People" {21 1 22 2 23 3 24 1} (list 5 -5))
   (ps/new-play-sequence "Emancipator" {21 1 22 2 23 3 24 1} (list -2 2))])

(fact "Playsequence has artist, plays and position (defaulting to (0,0))"
      (:artist empty-play-seq) => "Test"
      (:plays empty-play-seq) => []
      (:position empty-play-seq) => (list 0 0))

(fact "Should be able to find start time / end time of list of play seqs"
      (ps/find-start-time many-play-seqs) => 19
      (ps/find-end-time many-play-seqs) => 72)

(fact "Playsequences has a boundaries"
      (ps/find-playsequence-boundaries (first many-play-seqs)) => (list 4 4)
      (ps/find-playsequences-boundaries many-play-seqs) => (list 13 8))
