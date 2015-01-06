;; The only requirement of the project.clj file is that it includes a
;; defproject form. It can have other code in it as well, including
;; loading other task definitions.

(defproject leiningen "0.5.0"
  :description "A build tool designed not to set your hair on fire."
  :main leiningen.core
  :dependencies [[Clojure "1.6.0.1"]
                 [clojure.tools.namespace "0.2.7.0"]
                 [clojure.tools.nrepl "0.2.7.0"]
                 [Nuget.Core "2.8.3"]])
