#!/usr/bin/env bash
BASE_PATH=$(dirname "$0")
helm template $BASE_PATH/powerful-framew --name powerful-framew --namespace framew-demo
