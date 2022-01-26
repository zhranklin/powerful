#!/bin/bash
DOCKERFILE='
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LANGUAGE=C.UTF-8 LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update && apt-get install -y unzip wget nano vim expat net-tools less netcat lsof
'
mkdir tmp
tag=v1.4.1
for pair in base,openjdk:8u312 base-tomcat,tomcat:8.0.49-jre8; do
  arr=(${pair//,/ })
  tpe=${arr[0]}
  from=${arr[1]}
  image=zhranklin/powerful-$tpe
  mfargs="$image:$tag"
  for arch in amd64 arm64; do
    arch_img=$image:${tag}_linux_$arch
    echo "FROM $from$DOCKERFILE" | docker buildx build --platform linux/$arch --load tmp -f - -t $arch_img
    docker push $arch_img
    mfargs="$mfargs $arch_img"
  done
  docker manifest create --amend $mfargs
  docker manifest push $image:$tag
  docker manifest rm $image:$tag
done
rmdir tmp
