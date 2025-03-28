#!/bin/bash -x

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
echo $SCRIPT_DIR

pushd ${SCRIPT_DIR}/linter
npm run lint -- "$@"
popd