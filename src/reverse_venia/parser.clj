(ns reverse-venia.parser
  (:require [graphql-clj.parser :as parser]))


(declare reduce-arguments)

(defn argument-value
  "Parses argument value node"
  [{:keys [tag value values fields]}]
  (cond
    (= tag :list-value)
    (mapv argument-value values)

    (= tag :object-value)
    (reduce-arguments fields)

    :default
    value))


(defn reduce-arguments
  "Reduces arguments vector into a map"
  [arguments]
  (reduce
    (fn [m {:keys [name value]}]
      (assoc m (keyword name) (argument-value value)))
    {}
    arguments))


(defn document-mapper
  "Recursively maps vector of graphql nodes"
  [{:keys [tag selection-set arguments name]}]
  (cond
    (or (= tag :query-definition)
        (= tag :mutation)
        (= tag :selection-set))
    (-> (mapv document-mapper selection-set)
        first)

    (= tag :selection-field)
    (let [fields   [(keyword name)
                    (when (some? arguments)
                      (reduce-arguments arguments))
                    (when (some? selection-set)
                      (mapv document-mapper selection-set))]
          fields' (filterv some? fields)]
      (if (= (count fields') 1)
        (first fields')
        fields'))))


(defn parse-graphql
  "Parses GraphQl query to Venia Clojure syntax"
  [query]
  (->> (parser/parse-query-document query)
       (mapv document-mapper)))
