#!/usr/bin/env bash
while [ $# -ge 1 ] ; do
  case "$1" in
    -reserve) RESERVE_IMAGE=1; shift 1; ;;
    -noinstall) NO_INSTALL=1; shift 1; ;;
    *) echo unsupported option: $1; exit 1; shift 1; ;;
  esac
done

