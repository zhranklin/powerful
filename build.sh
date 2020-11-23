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
git status
if git diff-index --quiet HEAD --; then
  commit=$(git rev-parse --short HEAD)
  tag=$(git show-ref --tags| grep $commit | awk -F"[/]" '{print $3}')
  if [ -z $tag ]; then
     tag=$commit
  fi
else
  tag=dev
fi

hub=zhranklin
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
    if [[ $module != "springboot-2" ]]; then
      docker rmi $hub/$image
    fi
  done
  docker tag $hub/powerful-sb2:$tag $hub/powerful:$tag
  docker push $hub/powerful:$tag
  docker rmi $hub/powerful-sb2:$tag
  docker rmi $hub/powerful:$tag
  docker rmi zhranklin/powerful-base-java
  docker rmi zhranklin/powerful-base-tomcat
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
  git status
  SDK_IMAGE=$(./build.sh -noinstall)
fi
cd -
if [[ $BUILD_OPERATOR = "1" ]]; then
  if [[ $SDK_IMAGE =~ .*:dev ]]; then
    SED_CMD='1c\
      FROM '$SDK_IMAGE
    tag=dev
  else
    SED_CMD=' '
  fi
  OPERATOR_IMAGE="zhranklin/powerful-operator:$tag"
  cat operator/build/Dockerfile | sed "$SED_CMD" | docker build operator -t $OPERATOR_IMAGE -f -
  docker push $OPERATOR_IMAGE
  docker rmi $OPERATOR_IMAGE
  if [[ $BUILD_OPERATOR_SDK = "1" ]]; then
    docker rmi $SDK_IMAGE
  fi
fi
