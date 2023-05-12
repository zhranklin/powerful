FROM zhranklin/helm-operator:v1.5.8-jdk-all

ADD ./jars/*.jar /opt/helm/
COPY watches.yaml ${HOME}/watches.yaml
COPY helm-charts/ ${HOME}/helm-charts/
COPY dubbo-jars/dubbo26x /usr/local/dubbo26x
COPY dubbo-jars/dubbo27x /usr/local/dubbo27x

ARG imageTag="UNKNOWN"
ARG bundleSuffix=""
ENV POWERFUL_TAG=$imageTag
ENV POWERFUL_BUNDLE=$bundleSuffix
COPY bin /usr/local/bin
RUN /usr/local/bin/setup.sh
RUN chmod 777 /root
USER root
ENTRYPOINT ["/usr/local/bin/start.sh"]
