FROM tomcat:8.0.49-jre8
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LANGUAGE=C.UTF-8 LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update && apt-get install -y unzip wget nano vim expat net-tools less netcat lsof
RUN rm -r webapps
RUN sed -i 's/"8080"/"${server.port}"/g' conf/server.xml
COPY ./target/powerful-spring-mvc.war webapps/ROOT.war
