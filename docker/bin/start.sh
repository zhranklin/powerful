#!/bin/bash
case "$1" in
  demo) RUN_DEMO=1; shift 1; ;;
  operator) RUN_OPERATOR=1; shift 1; ;;
  *) echo unsupported option: $1; shift 1; ;;
esac

if [[ $RUN_DEMO == "1" ]]; then
  JDK=${JDK:-17}
  export JAVA_HOME=/usr/local/openjdk-$JDK
  export PATH=$JAVA_HOME/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
  SB_VERSION=${SB_VERSION:-2.7.7}
  JAR=/opt/helm/powerful-boot-$SB_VERSION-java$JDK.jar

  # copy dubbo.jar into powerful.jar
  jar -xvf $JAR BOOT-INF/lib
  DUBBO_VERSION=${DUBBO_VERSION:-2.6.11}
  if [ $DUBBO_VERSION == "2.6.11" ]; then
    cp -r /usr/local/dubbo26x/* /BOOT-INF/lib/
  elif [ $DUBBO_VERSION == "2.7.22" ]; then
    cp -r /usr/local/dubbo27x/* /BOOT-INF/lib/
  fi
  jar -uf0 $JAR /BOOT-INF/lib

  java $(echo "$JAVA_OPTS" | sed 's/-javaagent.*nsf[^ ]*\( \|$\)/ /g') -jar $JAR stage0
  jar -uf $JAR -C /usr/local/javalib BOOT-INF/classes
  java $JAVA_OPTS -jar $JAR "$@"
fi

if [[ $RUN_OPERATOR == "1" ]]; then
  cd $HOME
  exec helm-operator exec-entrypoint helm --watches-file=$HOME/watches.yaml "$@"
fi
