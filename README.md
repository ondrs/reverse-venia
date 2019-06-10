# reverse-venia

GraphQL to Venia Clojure syntax query generation
https://reverse-venia.herokuapp.com

## Overview

Writing GraphQL queries using [GraphiQL](https://github.com/graphql/graphiql) is nice.
Auto suggestion works well, code is nicely highlighted and can be reformatted with a single click of a button.

However, while write GraphQL in pure Clojure we don't have such a luxury. 
We can use [Venia](https://github.com/Vincit/venia) library to write queries using Clojure data structures but this won't be good for prototyping.
GraphiQL with its features will be much faster.

Therefore you open your browser with GraphiQL connected to the latest GraphQL schema and start typing.
After you are done with your complicated nested query or mutation you can end up with something like this.

```graphql
mutation {
  property_create(external_id: "test-property", name: "test-property", email: "jesse@emaıl.com", options: {deposit: {business_name: "6 Nottingham", external_account: {object: "bank_account", country: "US", currency: "usd", account_number: "000123456789", routing_number: "110000000", account_holder_type: "individual", account_holder_name: "Holder of Account"}, payout_schedule: {interval: "daily"}, tos_acceptance: {date: 1558351184, ip: "45.33.63.187"}}, ops: {business_name: "6 Nottingham", external_account: {object: "bank_account", country: "US", currency: "usd", account_number: "000123456789", routing_number: "110000000", account_holder_type: "individual", account_holder_name: "Holder of Account"}, payout_schedule: {interval: "daily"}, tos_acceptance: {date: 1558351184, ip: "45.33.63.187"}}}) {
    id
    payment {
      is_active
      is_canceled
      billing_start
      canceled_on
      fee_percent
      payment_type
      customer {
        id
        name
        plan {
          id
          amount
          date
          source {
            id
            type
          }
        }
      }
    }
  }
}
```

Ok, what now?

You are lazy and you don't want to just blindly rewrite it into Venia Clojure syntax.
You can image the final result. Lots of typos, wrongly nested vectors and maps.
It will be just too much work.

Do you really thing you can produce this code easily and without any errors? 

```clojure
[[:property_create
  {:external_id "test-property",
   :name        "test-property",
   :email       "jesse@emaıl.com",
   :options
                {:deposit
                 {:business_name   "6 Nottingham",
                  :external_account
                                   {:object              "bank_account",
                                    :country             "US",
                                    :currency            "usd",
                                    :account_number      "000123456789",
                                    :routing_number      "110000000",
                                    :account_holder_type "individual",
                                    :account_holder_name "Holder of Account"},
                  :payout_schedule {:interval "daily"},
                  :tos_acceptance  {:date 1558351184, :ip "45.33.63.187"}},
                 :ops
                 {:business_name   "6 Nottingham",
                  :external_account
                                   {:object              "bank_account",
                                    :country             "US",
                                    :currency            "usd",
                                    :account_number      "000123456789",
                                    :routing_number      "110000000",
                                    :account_holder_type "individual",
                                    :account_holder_name "Holder of Account"},
                  :payout_schedule {:interval "daily"},
                  :tos_acceptance  {:date 1558351184, :ip "45.33.63.187"}}}}
  [:id
   [:payment
    [:is_active
     :is_canceled
     :billing_start
     :canceled_on
     :fee_percent
     :payment_type
     [:customer
      [:id
       :name
       [:plan [:id :amount :date [:source [:id :type]]]]]]]]]]]
```

Luckily, there is a great tool for lazy Clojure developers!

Just go to https://reverse-venia.herokuapp.com, dump your code in, and copy & past it back.

**Job done!**


## Docker

https://cloud.docker.com/repository/docker/ondrs/reverse-venia

```bash
docker run -p 80:80 ondrs/reverse-venia
```


## Local development

Start your REPL.
In `dev/user.clj` run 
```clojure
(go)
```

Web server runs at http://localhost:3448.
Figwheel at http://localhost:3449


## Deployment to Heroku

```bash
heroku container:push web
heroku container:release web
```
