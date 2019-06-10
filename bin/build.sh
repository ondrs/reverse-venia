#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/..

yarn install --modules-folder ${DIR}/../resources/public/node_modules

lein uberjar
