#!/bin/bash

set -e

IDF_REV=138c941fad431d3ef2ccbe3eaabf08b96cdc4d0b

if [ "x${IDF_PATH}x" == "xx" ]; then
  echo 'IDF_PATH isn'\''t set. Fix it!'
  exit 1
fi

set -x

cd $IDF_PATH
git fetch
git checkout --recurse-submodules $IDF_REV