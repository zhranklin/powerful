#!/bin/bash
docker buildx build -t harbor.cloud.netease.com/qztest/webflux:provider-27 --platform=linux/arm64,linux/amd64 . --push