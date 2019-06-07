(ns reverse-venia.parser-test
  (:require [clojure.test :refer :all]
            [reverse-venia.parser :as parser]))


(def sample-query
  "query {\n  human (id: 123) {\n    id\n    name\n    friends {\n      id\n      name\n      friends {\n        id\n      }\n    }\n  }\n}")

(def sample-mutation
  "mutation {\n  property_create(external_id: \"test-property\", name: \"test-property\", email: \"jesse@starcity.com\", options: {deposit: {business_name: \"6 Nottingham\", external_account: {object: \"bank_account\", country: \"US\", currency: \"usd\", account_number: \"000123456789\", routing_number: \"110000000\", account_holder_type: \"individual\", account_holder_name: \"Holder of Account\"}, payout_schedule: {interval: \"daily\"}, tos_acceptance: {date: 1558351184, ip: \"45.33.63.187\"}}, ops: {business_name: \"6 Nottingham\", external_account: {object: \"bank_account\", country: \"US\", currency: \"usd\", account_number: \"000123456789\", routing_number: \"110000000\", account_holder_type: \"individual\", account_holder_name: \"Holder of Account\"}, payout_schedule: {interval: \"daily\"}, tos_acceptance: {date: 1558351184, ip: \"45.33.63.187\"}}}) {\n    id\n  }\n}\n")


(deftest reduce-arguments-test
  (testing "should reduce arguments vector into a flat map"
    (let [result (parser/reduce-arguments
                   [{:tag :argument, :name "id", :value {:tag :string-value, :image "\"1002\"", :value "1002"}}])]
      (is (= result)
          {:id "1002"})))

  (testing "should reduce arguments vector into a nested map"
    (let [result (parser/reduce-arguments
                   [{:tag   :argument,
                     :name  "id",
                     :value {:tag :object-value, :fields [{:tag :object-field, :name "uuid", :value {:tag :int-value, :image "132", :value 132}}
                                                          {:tag :object-field, :name "hash", :value {:tag :int-value, :image "132", :value "465ew4f321gf32ds1g32aw1f32a1"}}]}}])]
      (is (= result
             {:id {:uuid 132, :hash "465ew4f321gf32ds1g32aw1f32a1"}})))))


(deftest parse-graphql-test
  (testing "should parse sample query"
    (is (= (parser/parse-graphql sample-query)
           [[:human {:id 123} [:id :name [:friends [:id :name [:friends [:id]]]]]]])))

  (testing "should parse complex mutation query"
    (is (= (parser/parse-graphql sample-mutation)
           [[:property_create
             {:external_id "test-property",
              :name        "test-property",
              :email       "jesse@starcity.com",
              :options     {:deposit {:business_name    "6 Nottingham",
                                      :external_account {:object              "bank_account",
                                                         :country             "US",
                                                         :currency            "usd",
                                                         :account_number      "000123456789",
                                                         :routing_number      "110000000",
                                                         :account_holder_type "individual",
                                                         :account_holder_name "Holder of Account"},
                                      :payout_schedule  {:interval "daily"},
                                      :tos_acceptance   {:date 1558351184, :ip "45.33.63.187"}},
                            :ops     {:business_name    "6 Nottingham",
                                      :external_account {:object              "bank_account",
                                                         :country             "US",
                                                         :currency            "usd",
                                                         :account_number      "000123456789",
                                                         :routing_number      "110000000",
                                                         :account_holder_type "individual",
                                                         :account_holder_name "Holder of Account"},
                                      :payout_schedule  {:interval "daily"},
                                      :tos_acceptance   {:date 1558351184, :ip "45.33.63.187"}}}}
             [:id]]]))))
