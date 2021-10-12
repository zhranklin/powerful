#!/bin/bash
BUILD_OPERATOR=1
COMPILE_DEMO=1
while [ $# -ge 1 ] ; do
  case "$1" in
    -all) BUILD_POWERFUL_ALL=1; shift 1; ;;
    -sdk) BUILD_OPERATOR_SDK=1; BUILD_OPERATOR=0; shift 1; ;;
    -n) COMPILE_DEMO=0; shift 1; ;;
    *)  echo unsupported option: $1; shift 1; ;;
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
if [[ $BUILD_OPERATOR == "1" && $COMPILE_DEMO == "1" ]]; then
  mvn clean install -DskipTests
fi
if [[ $BUILD_POWERFUL_ALL = "1" ]]; then
  modules="spring-mvc springboot-1"
  for module in $modules; do
    image=powerful-$(echo $module | sed 's/springboot-/sb/g; s/spring-//g'):$tag
    docker build ./powerful-$module -t $hub/$image
    docker push $hub/$image
    docker rmi $hub/$image
  done
fi
cd ./operator-sdk
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
  if [[ $SDK_IMAGE =~ .*:dev\$ ]]; then
    SED_CMD='1c\
      FROM '$SDK_IMAGE
    tag=dev
  else
    SED_CMD=' '
  fi
  OPERATOR_IMAGE="zhranklin/powerful:$tag"
  cp ./powerful-springboot-2/target/powerful-springboot-2.jar ./docker/app.jar
  cat docker/build/Dockerfile | sed "$SED_CMD" | docker build docker --build-arg imageTag="$tag" -t $OPERATOR_IMAGE -f -
  rm -f ./docker/app.jar
  docker push $OPERATOR_IMAGE
  docker rmi $OPERATOR_IMAGE
  if [[ $BUILD_OPERATOR_SDK = "1" ]]; then
    docker rmi $SDK_IMAGE
  fi
fi
docker rmi zhranklin/powerful-base
docker rmi zhranklin/powerful-base-tomcat
