(ns lastfm-visualizer.json-scrobble-parser
  (:use [lastfm-visualizer.scrobble :only [new-scrobble]])
  (:use [clojure.data.json :only [read-str]]))

(defn parse-scrobble
  [lastfm-scrobble]
  (new-scrobble
   (-> lastfm-scrobble
       (get "timestamp")
       (get "unixtimestamp"))
   (-> lastfm-scrobble
       (get "track")
       (get "artist")
       (get "name"))))
       
(defn parse-json
  [json-str]
  (map parse-scrobble (read-str json-str)))

(defn parse-json-file
  [path]
  (parse-json (slurp path)))


