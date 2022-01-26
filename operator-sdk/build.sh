#!/usr/bin/env bash
while [ $# -ge 1 ] ; do
  case "$1" in
    -reserve) RESERVE_IMAGE=1; shift 1; ;;
    -noinstall) NO_INSTALL=1; shift 1; ;;
    *) echo unsupported option: $1; exit 1; shift 1; ;;
  esac
done
IMAGE=zhranklin/helm-operator
branch=$(git symbolic-ref --short -q HEAD)
commit=$(git rev-parse --short HEAD)
tag=$(git show-ref --tags| grep $commit | awk -F"[/]" '{print $3}')
if [ -z $tag ]; then
   TAG=$branch-$commit
else
   TAG=$tag
fi
if [ -n "$(git status --porcelain .)" ]; then
  TAG=dev
fi

if [[ $NO_INSTALL == "1" ]]; then
  echo $IMAGE:$TAG
  exit
fi

make image-build-helm-amd64
make image-build-helm-arm64

QUEY=quay.io/operator-framework/helm-operator:dev
mfargs="$IMAGE:$TAG"
for _arch in _linux_amd64 _linux_arm64; do
  docker tag $QUEY$_arch $IMAGE:$TAG$_arch
  docker push $IMAGE:$TAG$_arch
  docker rmi $QUEY$_arch
  mfargs="$mfargs $IMAGE:$TAG$_arch"
done
docker manifest create --amend $mfargs
docker manifest push $IMAGE:$TAG
docker manifest rm $IMAGE:$TAG
if [[ $RESERVE_IMAGE != "1" ]]; then
  docker rmi $mfargs
fi
