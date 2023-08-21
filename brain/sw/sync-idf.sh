#!/bin/bash

set -e

IDF_REV=v5.1

if [ "x${IDF_PATH}x" == "xx" ]; then
  echo 'IDF_PATH isn'\''t set. Fix it!'
  exit 1
fi

set -x

(
    cd $IDF_PATH
    git fetch
    git checkout $IDF_REV
    git submodule update --init
)
