(ns leiningen.core
  (:gen-class)
  (:require [clojure.clr.io :as io]
            [clojure.clr.shell :as shell]))

(def project nil)

(defmacro defproject [project-name version & args]
  ;; This is necessary since we must allow defproject to be eval'd in
  ;; any namespace due to load-file; we can't just create a var with
  ;; def or we would not have access to it once load-file returned.
  `(do (alter-var-root #'project
                       (fn [_#]
                         (assoc (apply hash-map (quote ~args))
                           :name ~(name project-name)
                           :group ~(or (namespace project-name)
                                       (name project-name))
                           :version ~version
                           :root ~(str (.Directory (System.IO.FileInfo. *file*))))))
       (def ~(symbol (name project-name)) project)))

(defn bootstrap
  []
  (let [path (Environment/GetEnvironmentVariable "NULEIN_ASM")
        nuget-dll "Nuget.Core.2.8.3/lib/net40-Client/NuGet.Core.dll"]
    (if (.Exists (System.IO.DirectoryInfo. "Nuget.Core.2.8.3"))
      (assembly-load-file nuget-dll)
      (when (zero? (:exit (shell/sh "nuget" "install" "Nuget.Core")))
        (assembly-load-file nuget-dll)))
    path))

(defn read-project
  ([] (read-project "project.clj"))
  ([file]
   (binding [*ns* (find-ns 'clojure.core)]
     (require '[leiningen.core :refer [defproject]])
     (load-file file))
   project))

(def aliases {"--help" "help" "-h" "help" "-?" "help"})

(defn -main [command & args]
  (bootstrap)
  (let [command (or (aliases command) command)
        action-ns (symbol (str "leiningen." command))
        _ (require action-ns)
        action (ns-resolve action-ns (symbol command))
        project (read-project)]
    (binding [*compile-path* (or (:compile-path project)
                                 (str (:root project) "."))]
      (apply action project args)
      (shutdown-agents))))
