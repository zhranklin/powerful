#!/bin/bash
set -eux
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

COMPILE_DEMO=1
BUNDLE=0
BUILD_SDK=0
USE_NEWEST_SDK=0
BUILD_IMAGE=1

# 默认选项: 使用已推送的operator-sdk镜像作为基础, 输出2.7 on java17镜像
while [ $# -ge 1 ] ; do
  case "$1" in
    -b) BUNDLE=1; shift 1; ;; # 输出bundle
    -cn) COMPILE_DEMO=0; shift 1; ;; # 不编译
    -s)  BUILD_SDK=1; USE_NEWEST_SDK=1; shift 1; ;; # 使用本地最新的operator-sdk(重新编译)
    -sn) BUILD_SDK=0; USE_NEWEST_SDK=1; shift 1; ;; # 使用本地已编译的operator-sdk
    -in) BUILD_IMAGE=0; shift 1; ;; # 不输出镜像
    *)  echo unsupported option: $1; shift 1; ;;
  esac
done

cd `dirname $0`
git status

hub=${hub:-zhranklin}
tag=$(getTag)
sdkImage=$hub/helm-operator:$tag
if [[ $BUNDLE == "1" ]]; then
  sdkImage=$hub/helm-operator:$tag-jdk-all
fi

# Step 1: 重新构建operator-sdk, 修改go代码时需要
if [[ $BUILD_SDK = "1" ]]; then
  (cd operator-sdk; make build/operator-sdk-dev-amd64-linux-gnu build/operator-sdk-dev-arm64-linux-gnu)
  mfargs="$sdkImage"
  DOCKERFILE="$(cat docker/build/Dockerfile.sdk)"
  if [[ $BUNDLE != "1" ]]; then
    DOCKERFILE="${DOCKERFILE//-jdk-all/}"
  fi
  for arch in amd64 arm64; do
    arch_img=${sdkImage}_linux_$arch
    echo "$DOCKERFILE" | docker buildx build --platform "linux/$arch" --load operator-sdk -f - -t $arch_img
    docker push $arch_img
    docker rmi $arch_img
    mfargs="$mfargs $arch_img"
  done
  docker manifest create --amend $mfargs
  docker manifest push $sdkImage
  docker manifest rm $sdkImage
  if [[ $BUILD_IMAGE != "1" ]]; then
    docker rmi $mfargs
  fi
fi

# Step 2: 编译demo, 本地修改过demo的java代码, 或第一次构建时需要
if [[ $COMPILE_DEMO = 1 ]]; then
  export boots_java17="2.7.7"
  export jdks="17"
  # 如果指定了bundle版本, 则会编译所有的jar包, 否则只编译2.7 on java17
  if [[ $BUNDLE == "1" ]]; then
    export boots_java8="1.5.22.RELEASE 2.0.9.RELEASE 2.1.18.RELEASE 2.2.13.RELEASE 2.6.14 2.7.7"
    export boots_java11="2.1.18.RELEASE 2.2.13.RELEASE 2.6.14 2.7.7"
    export boots_java17="2.6.14 2.7.7"
    export jdks="8 11 17"
  fi
  rm -rf docker/jars/*.jar
  for jdk in $jdks; do
    JavaVersion=$jdk
    (cd powerful-core; mvn clean install "-DJavaVersion=${JavaVersion}" $(test "$(uname)" = "Darwin" && echo  -Dos.detected.classifier=osx-x86_64))
    versions=$(eval "echo \$boots_java$jdk")
    for version in $versions; do
      echo "version: $version, jdk: $jdk, JavaVersion: $JavaVersion"
      (cd powerful-springboot; mvn clean install "-DJavaVersion=${JavaVersion}" -Dspringboot.version=$version)
      cp powerful-springboot/target/powerful-boot-$version-java$JavaVersion.jar docker/jars
    done
  done
fi

# Step 3: 构建镜像, 镜像包含: operator-sdk(在基础镜像中)、powerful自动渲染的chart、powerful demo的jar包
if [[ $BUILD_IMAGE = "1" ]]; then
  # 下载dubbo2.6.11和2.7.22的相关依赖
  mvn -f powerful-core/dubbo27pom.xml dependency:copy-dependencies -DincludeScope=provided -DincludeTypes=jar -DoutputDirectory=../docker/dubbo-jars/dubbo27x
  mvn -f powerful-core/dubbo26pom.xml dependency:copy-dependencies -DincludeScope=provided -DincludeTypes=jar -DoutputDirectory=../docker/dubbo-jars/dubbo26x
  # 下载tomcat和宝兰德的相关依赖
  mvn -f powerful-core/bes_dep.xml dependency:copy-dependencies -DincludeScope=provided -DincludeTypes=jar -DoutputDirectory=../docker/containers-jars/bes_dep
  mvn -f powerful-core/tomcat_dep.xml dependency:copy-dependencies -DincludeScope=provided -DincludeTypes=jar -DoutputDirectory=../docker/containers-jars/tomcat_dep
  mvn -f powerful-core/tongweb_dep.xml dependency:copy-dependencies -DincludeScope=provided -DincludeTypes=jar -DoutputDirectory=../docker/containers-jars/tongweb_dep

  if [[ $USE_NEWEST_SDK = "1" ]]; then
    SED_CMD='1c\
      FROM '$sdkImage
  else
    SED_CMD=' '
  fi
  if [[ $BUNDLE != "1" ]]; then
    SED_CMD="$SED_CMD
             ;s#\./jars/\*\.jar#./jars/powerful-boot-2.7.7-java17.jar#g; s/-jdk-all//g"
  fi
  OPERATOR_IMAGE="$hub/powerful:$tag"
  if [[ $BUNDLE == "1" ]]; then
    OPERATOR_IMAGE="$hub/powerful-bundle:$tag"
  fi
  mf_args="$OPERATOR_IMAGE"
  for arch in amd64 arm64; do
    _arch=_linux_$arch
    if [[ $BUNDLE == "1" ]]; then
      suffix=-bundle
    else
      suffix=
    fi
    cat docker/build/Dockerfile | sed "$SED_CMD" | docker buildx build --platform "linux/$arch" --load docker --build-arg imageTag="$tag" --build-arg bundleSuffix=$suffix -t $OPERATOR_IMAGE$_arch -f -
    docker push $OPERATOR_IMAGE$_arch
    docker rmi $OPERATOR_IMAGE$_arch
    mf_args="$mf_args $OPERATOR_IMAGE$_arch"
  done
  docker manifest create --amend $mf_args
  docker manifest push $OPERATOR_IMAGE
  rm -rf ./docker/dubbo-jars
  rm -f ./docker/app.jar
  rm -rf ./docker/containers-jars
  docker push $OPERATOR_IMAGE
  docker rmi $OPERATOR_IMAGE
  if [[ $BUILD_SDK = "1" ]]; then
    docker rmi $sdkImage
  fi
fi
