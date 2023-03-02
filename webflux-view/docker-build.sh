#!/bin/bash
mvn clean install -Dmaven.test.skip=true
docker buildx build -t harbor.cloud.netease.com/qztest/nsf-demo-stock-viewer:webflux-26 --platform=linux/amd64 . --push