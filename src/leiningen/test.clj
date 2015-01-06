(ns leiningen.test
  "Run the project's tests."
  (:refer-clojure :exclude [test])
  (:use [clojure.test])
  (:require [clojure.clr.io :as io]
            [leiningen.util :as util]))

(defonce old-test-var test-var)

(defn test-var-matching [pred var]
  (when (pred (meta var))
    (old-test-var var)))

(defn merge-predicates [preds]
  (fn [t] (every? #(% t) preds)))

(defn run-matching [project preds]
  (binding [test-var (partial test-var-matching (merge-predicates preds))]
    (doseq [n (util/find-namespaces-in-dir (str "test/" (:root project)))]
      (require n)
      (run-tests n))))

(defn test
  "Run the projects tests. Accept a list of predicates called with each test
var's metadata. Does not support anonymous fns; works best with keywords."
  [project & args]
  (let [preds (if (empty? args)
                [identity]
                (map (comp eval read-string) args))]
    (run-matching project preds)))
