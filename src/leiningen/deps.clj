(ns leiningen.deps
  "Install assemblies for all dependencies in lib."
  (:require [clojure.clr.io :as io])
  (:import [NuGet PackageRepositoryFactory IPackageRepository PackageManager]
           [System.IO DirectoryInfo Path]))

(defn ^IPackageRepository make-repository
  [^String url]
  (.CreateRepository (PackageRepositoryFactory/Default) url))

(defn package-matches?
  [id version package]
  (and (= id (.Id package))
       (= version (.Version package))))

(defn resolve-package
  [id version repositories]
  (reduce (fn [_ [_ url]]            
            (let [repo (make-repository url)
                  packages (.FindPackagesById repo id)]
              (when-let [package (first (filter #(package-matches? id version %)
                                                packages))]
                (reduced [repo package]))))
          nil repositories))

(defn deps
  [project]
  (let [{:keys [dependencies repositories]} project
        target (doto (DirectoryInfo. (:target-path project))
                 (.Create))
        target-path (.FullName target)]
    (doseq [[id version] dependencies
            :let [id (name id)]]
      (if-let [[repo pkg] (resolve-package id version repositories)]
        (let [manager (PackageManager. repo target-path)
              semver (NuGet.SemanticVersion/Parse version)]
          (.InstallPackage manager id semver))))))

(def sample-deps
  '{:dependencies [[Clojure "1.6.0"]
                   [Nuget.Core "2.8.3"]]
    :repositories {"nuget-api-v2" "https://packages.nuget.org/api/v2"}
    :target-path "/Users/adrian/workspace/clr/leiningen/target"})
