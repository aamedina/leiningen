(ns leiningen.install
  "Install the project in your local repository. Currently requires Maven."
  (:require [clojure.clr.shell :refer [sh with-sh-dir]]))

(defn install
  "Install the project and its dependencies"
  [project & args])
