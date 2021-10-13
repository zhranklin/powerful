FROM zhranklin/helm-operator:v1.3.0

ADD ./app.jar /opt/helm/app.jar
COPY watches.yaml ${HOME}/watches.yaml
COPY helm-charts/ ${HOME}/helm-charts/

ARG imageTag="UNKNOWN"
ENV POWERFUL_TAG=$imageTag
COPY bin /usr/local/bin
RUN /usr/local/bin/setup.sh
USER ${USER_UID}
ENTRYPOINT ["/usr/local/bin/start.sh"]
