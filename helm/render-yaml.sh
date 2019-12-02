#!/usr/bin/env bash
BASE_PATH=$(dirname "$0")
helm template $BASE_PATH/framew-e2e --name powerful-framew --namespace framew-demo
