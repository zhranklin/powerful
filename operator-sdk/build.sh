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

make image-build-helm

QUEY=quay.io/operator-framework/helm-operator:dev
docker tag $QUEY $IMAGE:$TAG
docker push $IMAGE:$TAG
docker rmi $QUEY
if [[ $RESERVE_IMAGE != "1" ]]; then
  docker rmi $IMAGE:$TAG
fi
