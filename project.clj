;; The only requirement of the project.clj file is that it includes a
;; defproject form. It can have other code in it as well, including
;; loading other task definitions.

(defproject leiningen "0.5.0"
  :description "A build tool designed not to set your hair on fire."
  :main leiningen.core
  :dependencies [[Clojure "1.6.0.1"]
                 [Nuget.Core "2.8.3"]]
  :aot [leiningen.core
        leiningen.util
        leiningen.clean
        leiningen.test
        leiningen.help
        leiningen.compile
        leiningen.deps
        leiningen.install])
