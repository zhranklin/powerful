#!/bin/bash
docker buildx build -t harbor.cloud.netease.com/qztest/nsf-demo-stock-advisor:webflux-26 --platform=linux/arm64,linux/amd64 . --push