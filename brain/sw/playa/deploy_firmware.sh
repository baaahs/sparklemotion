#!/usr/bin/env bash

set -e

# This _should_ be what the idf does but pay attention in case
# they change it. To see what they are doing look in
# ~/esp/esp-idf/tools/cmake/third_party/GetGitRevisionDescription.cmake

VER=`git describe --always --tags --dirty`

FWDIR=~/sparklemotion/fw
echo FWDIR is \'${FWDIR}\'
mkdir -p ${FWDIR}

OUTPUT="${FWDIR}/${VER}.bin"

cp build/brain.bin "${OUTPUT}"

echo Deployed firmware to ${OUTPUT}
