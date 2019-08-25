#!/usr/bin/env bash

set -e

# This _should_ be what the idf does but pay attention in case
# they change it. To see what they are doing look in
# ~/esp/esp-idf/tools/cmake/third_party/GetGitRevisionDescription.cmake

VER=`git describe --always --tags --dirty`
VER=$( date "+%Y%m%d%H%M%S" )
SHA=$( shasum build/brain.bin | cut -c1-8)

FWDIR=~/sparklemotion/fw
echo FWDIR is \'${FWDIR}\'
mkdir -p ${FWDIR}

OUTPUT="${FWDIR}/${VER}-${SHA}.bin"

cp build/brain.bin "${OUTPUT}"

echo Deployed firmware to ${OUTPUT}
