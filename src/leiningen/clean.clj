(ns leiningen.clean
  "Remove compiled files and dependencies from project."
  (:require [clojure.clr.io :as io])
  (:import [System.IO DirectoryInfo]))

(defn clean [project]
  (let [dir (DirectoryInfo. (:target-path project))]
    (when (.Exists dir)
      (.Delete dir))))
