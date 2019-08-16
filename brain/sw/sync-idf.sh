#!/bin/bash

set -e

IDF_REV=1271008dd80a75dd564e425bc76f3e6684692830

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