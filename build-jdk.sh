#!/bin/bash
tag=v0.10.0-jdk-all
image=${image:-harbor.cloud.netease.com/qztest/powerful-jdk-all}
branch=$(git symbolic-ref --short -q HEAD)
dockerfile=docker/build/Dockerfile.jdk-all

mfargs="$image:$tag"
for arch in amd64 arm64; do
  arch_img=$image:${tag}_linux_$arch
  cat $dockerfile | docker buildx build --platform linux/$arch --load . -f - -t $arch_img
  docker push $arch_img
  mfargs="$mfargs $arch_img"
done
docker manifest create --amend $mfargs
docker manifest push $image:$tag
docker manifest rm $image:$tag
