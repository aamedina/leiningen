(ns leiningen.util
  (:require [clojure.string :as str]
            [clojure.clr.io :as io])
  (:import [System.IO DirectoryInfo Path]
           [clojure.lang PushbackTextReader]))

(defn env
  [var]
  (Environment/GetEnvironmentVariable (name var)))

(defn add-path
  [var path]
  (Environment/SetEnvironmentVariable (name var)
                                      (str (env var) ":" path)))

(defn add-to-load-path
  [path]
  (add-path "CLOJURE_LOAD_PATH" path))

(defn add-to-compile-path
  [path]
  (set! *compile-path* (str *compile-path* ":" path)))

(defn load-path
  []
  (env "CLOJURE_LOAD_PATH"))

(defn ns-decl
  [file]
  (with-open [rdr (PushbackTextReader. (io/text-reader file))]
    (->> (repeatedly #(read rdr false nil false))
         (filter (every-pred seq? #(= 'ns (first %))))
         first
         second)))

(defn find-namespaces-in-dir
  [dir]
  (let [dir (if (instance? DirectoryInfo dir)
              dir
              (DirectoryInfo. dir))]
    (when (.Exists dir)
      (concat (mapcat find-namespaces-in-dir (.GetDirectories dir))
              (->> (.GetFiles dir)
                   (filter #(= (Path/GetExtension (.Name %)) ".clj"))
                   (map ns-decl))))))

(defn find-namespaces-on-load-path
  []
  (reduce (fn [xs path]
            (into xs (find-namespaces-in-dir path)))
          [] (->> (str/split (env "CLOJURE_LOAD_PATH") #":")
                  (remove #(= (Path/GetExtension (.Name %)) ".jar")))))

(defn find-namespaces-on-compile-path
  []
  (find-namespaces-in-dir *compile-path*))
