FROM openjdk:8-jdk-alpine
# 根据编译生成jar所对应的jdk版本选择合适的基础镜像
#FROM openjdk:9
#FROM openjdk:11
#FROM openjdk:14
#FROM openjdk:17
COPY start.sh /usr/local/bin/
# 拷贝jar包至镜像中
ADD powerful-springboot-2-5.jar /sb2-5.jar
ADD powerful-springboot-2-6.jar /sb2-6.jar
ADD powerful-springboot-2-7.jar /sb2-7.jar
ENTRYPOINT ["/bin/sh", "/usr/local/bin/start.sh"]
