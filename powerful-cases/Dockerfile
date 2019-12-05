FROM openjdk:8u222
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LANGUAGE=C.UTF-8 LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update && apt-get install -y unzip wget nano vim expat net-tools less netcat lsof
ADD target/powerful-cases-*.jar /app.jar
ENTRYPOINT java $JAVA_OPTS -jar /app.jar