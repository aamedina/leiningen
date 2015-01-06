(ns leiningen.compile
  "Compile the namespaces listed in project.clj or all namespaces in src."
  (:refer-clojure :exclude [compile])
  (:require [leiningen.util :as util]))

(defn compile
  "Ahead-of-time compile the project. Looks for all namespaces under src/
unless a list of :namespaces is provided in project.clj."
  [project]
  (doseq [n (or (if (identical? (:aot project) :all)
                  (mapcat util/find-namespaces-in-dir (:source-paths project))
                  (:aot project)))]
    (println "Compiling" n)
    (clojure.core/compile n)))
