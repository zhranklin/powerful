FROM hub.c.163.com/qingzhou/amm-toolbox:v0.1.0-java8_linux_amd64
ADD target/webflux-provider-2.6.14.war /app.jar
ENTRYPOINT java $JAVA_OPTS -jar /app.jar