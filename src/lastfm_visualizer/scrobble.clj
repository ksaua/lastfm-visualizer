(ns lastfm-visualizer.scrobble)

(defrecord Scrobble [time artist])
(defn new-scrobble
  [time artist]
  (Scrobble. time artist))
