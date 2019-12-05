FROM zhranklin/powerful-base-tomcat
RUN rm -r webapps
RUN sed -i 's/"8080"/"${server.port}"/g' conf/server.xml
COPY ./target/powerful-spring-mvc.war webapps/ROOT.war
