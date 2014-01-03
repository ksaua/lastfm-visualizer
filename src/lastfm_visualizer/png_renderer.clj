(ns lastfm-visualizer.png-renderer
  (import java.io.File)
  (import java.awt.Color)
  (import java.awt.image.BufferedImage)
  (import javax.imageio.ImageIO)
  (:require [lastfm-visualizer.playsequence :refer [find-start-time
                                                    find-end-time
                                                    find-playsequences-boundaries]]))


(defrecord Circle [position radius])
(defn new-circle
  [position radius]
  (Circle. position radius))

(defn ratioed-circle
  [circle ratio]
  (new-circle (map (partial * ratio) (:position circle)) (* ratio (:radius circle))))

(defn find-circles-for-time
  [play-seqs time]
  (->> (filter #(contains? (:plays %) time) play-seqs)
       (map #(Circle. (:position %) (get (:plays %) time)))))


(defn create-image
  [circles directory filename width height]
  (let [buffered-image (BufferedImage. width height BufferedImage/TYPE_INT_ARGB)
        graphics (.createGraphics buffered-image)
        file (File. (str directory "/" filename ".png"))
        hwidth (/ width 2)
        hheight (/ height 2)]
    (do
      (doseq [circle circles]
        (let [radius (:radius circle)
              hradius (/ radius 2)]
          (.fillOval graphics
                     (+ (- (nth (:position circle) 0) hradius) hwidth)
                     (+ (- (nth (:position circle) 1) hradius) hheight)
                     (:radius circle)
                     (:radius circle))))
      (ImageIO/write buffered-image "png" file))))


(defn render-ratio
  "To allow the render function to output any sized images, we need to
  calculate the ratio between image size and boundary size"
  [image-width image-height boundary-width boundary-height]
  (min (/ image-width boundary-width)
       (/ image-height boundary-height)))

(defn render
  [root-directory image-width image-height play-seqs step] 
  (let [start-time (find-start-time play-seqs)
        end-time (find-end-time play-seqs)
        [boundary-x boundary-y] (find-playsequences-boundaries play-seqs)
        ratio (render-ratio image-width image-height boundary-x boundary-y)]

    (doseq [[index time]   
            (map-indexed vector (range start-time (+ end-time 1) step))]
      (let [circles (find-circles-for-time play-seqs time)
            ratioed-circles (map ratioed-circle circles)]
        (create-image circles root-directory index image-width image-height)))))
