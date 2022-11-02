#!/bin/bash
function getTag() {
  commit=$(git rev-parse --short HEAD)
  tag=$(git show-ref --tags| grep $commit | awk -F"[/]" '{print $3}')
  if git diff-index --quiet HEAD --; then
    if [ -z $tag ]; then
       tag=$commit
    fi
  else
    tag=$commit-dirty
  fi
  echo $tag
}

while [ $# -ge 1 ] ; do
  case "$1" in
    -da) BUILD_ALL_DEMO=1; shift 1; ;;
    -dn) COMPILE_DEMO=0; shift 1; ;;
    -s)  BUILD_SDK=1; USE_NEWEST_SDK=1; shift 1; ;;
    -sn) BUILD_SDK=0; USE_NEWEST_SDK=1; shift 1; ;;
    -o) BUILD_OPERATOR=1; shift 1; ;;
    *)  echo unsupported option: $1; shift 1; ;;
  esac
done

if [[ $BUILD_SDK = "1" && $BUILD_OPERATOR = "" ]]; then
  BUILD_OPERATOR="0"
fi

BUILD_OPERATOR=${BUILD_OPERATOR:-1}
COMPILE_DEMO=${COMPILE_DEMO:-1}
BUILD_ALL_DEMO=${BUILD_ALL_DEMO:-0}
BUILD_SDK=${BUILD_SDK:-0}
USE_NEWEST_SDK=${USE_NEWEST_SDK:-0}

cd `dirname $0`
git status

hub=${hub:-zhranklin}
sdkImage=$hub/helm-operator
tag=$(getTag)

if [[ $BUILD_SDK = "1" ]]; then
  export HELM_BASE_IMAGE=$sdkImage
  export HELM_BASE_IMAGE_TAG=$tag
  (cd operator-sdk; make image-build-helm-amd64)
  (cd operator-sdk; make image-build-helm-arm64)

  mfargs="$sdkImage:$tag"
  for _arch in _linux_amd64 _linux_arm64; do
    docker push $sdkImage:$tag$_arch
    docker rmi $sdkImage:$tag$_arch
    mfargs="$mfargs $sdkImage:$tag$_arch"
  done
  docker manifest create --amend $mfargs
  docker manifest push $sdkImage:$tag
  docker manifest rm $sdkImage:$tag
  if [[ $BUILD_OPERATOR != "1" ]]; then
    docker rmi $mfargs
  fi
fi

if [[ $BUILD_OPERATOR = "1" ]]; then
  if [[ $COMPILE_DEMO = "1" ]]; then
    mvn clean install -DskipTests
  fi
  if [[ $USE_NEWEST_SDK = "1" ]]; then
    SED_CMD='1c\
      FROM '$sdkImage:$tag
  else
    SED_CMD=' '
  fi
  OPERATOR_IMAGE="$hub/powerful:$tag"
  cp ./powerful-springboot-2/target/powerful-springboot-2.jar ./docker/app.jar
  mf_args="$OPERATOR_IMAGE"
  for arch in amd64 arm64; do
    _arch=_linux_$arch
    cat docker/build/Dockerfile | sed "$SED_CMD" | docker buildx build --platform "linux/$arch" --load docker --build-arg imageTag="$tag" -t $OPERATOR_IMAGE$_arch -f -
    docker push $OPERATOR_IMAGE$_arch
    docker rmi $OPERATOR_IMAGE$_arch
    mf_args="$mf_args $OPERATOR_IMAGE$_arch"
  done
  docker manifest create $mf_args
  docker manifest push $OPERATOR_IMAGE
  rm -f ./docker/app.jar
  docker push $OPERATOR_IMAGE
  docker rmi $OPERATOR_IMAGE
  if [[ $BUILD_SDK = "1" ]]; then
    docker rmi $sdkImage:$tag
  fi
fi
if [[ $BUILD_ALL_DEMO = "1" ]]; then
  modules="springboot-1 springboot-2"
  for module in $modules; do
    # 不带java后缀的是java17
    for df in `ls ./powerful-$module/Dockerfile*`; do
      image=powerful$(echo "$df"|sed 's/.*Dockerfile//g')-$(echo $module | sed 's/springboot-/sb/g; s/spring-//g'):$tag
      mf_args="$hub/$image"
      for arch in amd64 arm64; do
        _arch=_$arch
        docker buildx build --platform "linux/$arch" --load ./powerful-$module -t $hub/$image$_arch -f $df
        docker push $hub/$image$_arch
        docker rmi $hub/$image$_arch
        mf_args="$mf_args $hub/$image$_arch"
      done
      docker manifest create --amend $mf_args
      docker manifest push $hub/$image
    done
  done
fi
docker rmi \
  zhranklin/powerful-base:v1.4.1_linux_amd64 \
  zhranklin/powerful-base-tomcat:v1.4.1_linux_amd64 \
  zhranklin/powerful-base:v1.4.1_linux_arm64 \
  zhranklin/powerful-base-tomcat:v1.4.1_linux_arm64
docker manifest rm \
  zhranklin/powerful-base:v1.4.1 \
  zhranklin/powerful-base-tomcat:v1.4.1 \
