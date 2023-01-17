FROM zhranklin/helm-operator:v1.5.0-jdk-all

ADD ./jars/*.jar /opt/helm/
COPY watches.yaml ${HOME}/watches.yaml
COPY helm-charts/ ${HOME}/helm-charts/

ARG imageTag="UNKNOWN"
ARG bundleSuffix=""
ENV POWERFUL_TAG=$imageTag
ENV POWERFUL_BUNDLE=$bundleSuffix
COPY bin /usr/local/bin
RUN /usr/local/bin/setup.sh
USER ${USER_UID}
ENTRYPOINT ["/usr/local/bin/start.sh"]
