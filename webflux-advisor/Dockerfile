FROM harbor.cloud.netease.com/qztest/tomcat-8.5-jdk8-openjdk-skiff:v1.6-220801-multi-arch-nsf-20230228
ADD target/webflux-advisor-2.7.5.jar /app.jar

ADD start.sh /start.sh

CMD ["sh","/start.sh"]