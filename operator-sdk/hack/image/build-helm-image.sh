#!/usr/bin/env bash

set -eux

source hack/lib/test_lib.sh
source hack/lib/image_lib.sh

ROOTDIR="$(pwd)"
TMPDIR="$(mktemp -d)"
trap_add 'rm -rf $TMPDIR' EXIT
BASEIMAGEDIR="$TMPDIR/helm-operator"
mkdir -p "$BASEIMAGEDIR"
echo $BASEIMAGEDIR

# build operator binary and base image
pushd "$BASEIMAGEDIR"

arch=$2
mkdir -p build/_output/bin/
cp $ROOTDIR/build/operator-sdk-dev-$arch-linux-gnu build/_output/bin/helm-operator
cp $ROOTDIR/../docker/build/Dockerfile.sdk build/Dockerfile

docker buildx build --platform "linux/$arch" --load . -f build/Dockerfile -t ${1}
# If using a kind cluster, load the image into all nodes.
load_image_if_kind "$1"
popd
