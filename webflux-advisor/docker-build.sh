#!/bin/bash
docker buildx build -t harbor.cloud.netease.com/qztest/webflux:advisor-27 --platform=linux/arm64,linux/amd64 . --push