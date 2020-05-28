#!/bin/bash
while [ $# -ge 1 ] ; do
  case "$1" in
    -p) BUILD_POWERFUL=1; shift 1; ;;
    -all) BUILD_POWERFUL=1; BUILD_POWERFUL_ALL=1; BUILD_OPERATOR=1; shift 1; ;;
    -sdk) BUILD_OPERATOR_SDK=1; shift 1; ;;
    -o) BUILD_OPERATOR=1; shift 1; break;;
    *)  echo unsupported option: $1 shift 1; ;;
  esac
done

cd `dirname $0`
if git diff-index --quiet HEAD --; then
  commit=$(git rev-parse --short HEAD)
  tag=$(git show-ref --tags| grep $commit | awk -F"[/]" '{print $3}')
  if [ -z $tag ]; then
     tag=$commit
  fi
else
  tag=dev
fi

hub=hub.c.163.com/qingzhou
if [[ $BUILD_POWERFUL = "1" ]]; then
  mvn install

  modules="springboot-2"

  if [[ $BUILD_POWERFUL_ALL = "1" ]]; then
    modules="$modules spring-mvc springboot-1"
  fi

  for module in $modules; do
    image=powerful-$(echo $module | sed 's/springboot-/sb/g; s/spring-//g'):$tag
    docker build ./powerful-$module -t $hub/$image
    docker push $hub/$image
    docker tag $hub/$image $hub/istio/$image
    docker push $hub/istio/$image
  done
  docker tag $hub/powerful-sb2:$tag $hub/powerful:$tag
  docker tag $hub/powerful-sb2:$tag $hub/istio/powerful:$tag
  docker push $hub/powerful:$tag
  docker push $hub/istio/powerful:$tag
fi

cd $GOPATH/src/github.com/operator-framework/operator-sdk
if [[ $BUILD_OPERATOR_SDK = "1" ]]; then
  if [[ $BUILD_OPERATOR = "1" ]]; then
    ./build.sh -reserve
  else
    ./build.sh
  fi
fi
if [[ $BUILD_OPERATOR = "1" ]]; then
  SDK_IMAGE=$(./build.sh -noinstall)
fi
cd -
if [[ $BUILD_OPERATOR = "1" ]]; then
  OPERATOR_IMAGE="zhranklin/powerful-operator:$tag"
  SED_CMD='1c\
    FROM '$SDK_IMAGE
  cat operator/build/Dockerfile | sed "$SED_CMD" | docker build operator -t $OPERATOR_IMAGE -f -
  docker push $OPERATOR_IMAGE
  docker rmi $OPERATOR_IMAGE
  if [[ $BUILD_OPERATOR_SDK = "1" ]]; then
    docker rmi $SDK_IMAGE
  fi
fi
