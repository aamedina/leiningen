(ns leiningen.compile
  "Compile the namespaces listed in project.clj or all namespaces in src."
  (:refer-clojure :exclude [compile])
  (:require [clojure.clr.io :as io]
            [clojure.string :as str])
  (:import [System.IO DirectoryInfo Path]
           [clojure.lang PushbackTextReader]))

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
    (concat (mapcat find-namespaces-in-dir (.GetDirectories dir))
            (->> (.GetFiles dir)
                 (filter #(= (Path/GetExtension (.Name %)) ".clj"))
                 (map ns-decl)))))

(defn compile
  "Ahead-of-time compile the project. Looks for all namespaces under src/
unless a list of :namespaces is provided in project.clj."
  [project]
  (doseq [n (or (if (identical? (:aot project) :all)
                  (find-namespaces-in-dir (:source-paths project))
                  (:aot project)))]
    (println "Compiling" n)
    (clojure.core/compile n)))
