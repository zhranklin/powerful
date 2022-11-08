#!/bin/bash
case "$1" in
  demo) RUN_DEMO=1; shift 1; ;;
  operator) RUN_OPERATOR=1; shift 1; ;;
  *) echo unsupported option: $1; shift 1; ;;
esac

if [[ $RUN_DEMO == "1" ]]; then
  java $(echo "$JAVA_OPTS" | sed 's/-javaagent.*nsf[^ ]*\( \|$\)/ /g') -jar /opt/helm/app.jar stage0
  jar -uf /opt/helm/app.jar -C /usr/local/javalib BOOT-INF/classes
  java $JAVA_OPTS -jar /opt/helm/app.jar "$@"
fi

if [[ $RUN_OPERATOR == "1" ]]; then
  cd $HOME
  exec helm-operator exec-entrypoint helm --watches-file=$HOME/watches.yaml "$@"
fi
