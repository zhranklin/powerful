#!/bin/bash
docker buildx build -t harbor.cloud.netease.com/qztest/webflux:provider-26 --platform=linux/arm64,linux/amd64 . --push