FROM harbor.cloud.netease.com/qztest/helm-operator:v1.5.16-jdk-all

ADD ./jars/*.jar /opt/helm/
COPY watches.yaml ${HOME}/watches.yaml
COPY helm-charts/ ${HOME}/helm-charts/
COPY dubbo-jars/dubbo26x /usr/local/dubbo26x
COPY dubbo-jars/dubbo27x /usr/local/dubbo27x
COPY containers-jars/bes_dep /usr/local/bes_dep
COPY containers-jars/tomcat_dep /usr/local/tomcat_dep
COPY containers-jars/tomcat10_dep /usr/local/tomcat10_dep
COPY containers-jars/tomcat8_dep /usr/local/tomcat8_dep
COPY containers-jars/tongweb_dep /usr/local/tongweb_dep
COPY jakarta-jars/springboot3 /usr/local/jakarta_dep

ARG imageTag="UNKNOWN"
ARG bundleSuffix=""
ENV POWERFUL_TAG=$imageTag
ENV POWERFUL_BUNDLE=$bundleSuffix
COPY bin /usr/local/bin
RUN /usr/local/bin/setup.sh
RUN chmod 777 /root
USER root
ENTRYPOINT ["/usr/local/bin/start.sh"]
