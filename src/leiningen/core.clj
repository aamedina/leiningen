(ns leiningen.core
  (:gen-class)
  (:require [clojure.clr.io :as io]))

(def project nil)

(defmacro defproject [project-name version & args]
  ;; This is necessary since we must allow defproject to be eval'd in
  ;; any namespace due to load-file; we can't just create a var with
  ;; def or we would not have access to it once load-file returned.
  `(do (alter-var-root #'project
                       (fn [_#] (assoc (apply hash-map (quote ~args))
                                  :name ~(name project-name)
                                  :group ~(or (namespace project-name)
                                              (name project-name))
                                  :version ~version
                                  :root ~(.Directory (io/as-file *file*)))))
       (def ~(symbol (name project-name)) project)))

(binding [*ns* (find-ns 'clojure.core)]
  (use '[leiningen.core :only [defproject]]))

(defn read-project
  ([] (read-project "project.clj"))
  ([file]
   (load-file file)
   project))

(def aliases {"--help" "help" "-h" "help" "-?" "help"})

(defn -main [command & args]
  (let [command (or (aliases command) command)
        action-ns (symbol (str "leiningen." command))
        _ (require action-ns)
        action (ns-resolve action-ns (symbol command))
        project (read-project)]
    (binding [*compile-path* (or (:compile-path project)
                                 (str (:root project) "/classes/"))]
      (apply action project args)
      (shutdown-agents))))
