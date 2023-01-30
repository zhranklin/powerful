# Powerful

## 简介

powerful 是一个用于 ServiceMesh 或 NSF 调用链路的测试框架及 demo，支持自定义服务间的调用链路；并支持对链路中的服务进行故障注入，包括返回异常和时延；同时也支持对服务之间的调用自定义请求头、请求参数；支持自定义服务调用的线程数和总次数等功能。

## 编译项目和构建镜像

- 如果在本地修改过 powerful 的 Java 代码后，则需要通过此步骤重新编译项目，构建镜像并推送至仓库；
- 具体操作步骤如下：
  - 进入 powerful 的根目录，查看 maven 依赖的 jdk 版本。注意：需要保证 maven 依赖的 jdk 版本不低于后续编译 powerful 所需的 jdk版本，否则编译过程可能出错。
     ```
     mvn -v
     ```
  - 通过运行`build.sh`脚本文件，自动化地完成以下三个步骤：
    1. 重新构建operator-sdk；
    2. 编译项目生成Jar包；
    3. 构建镜像并上传。

    运行`build.sh`脚本文件的默认行为是：使用已推送的operator-sdk镜像作为基础, 输出基于`springboot 2.7`和`JDK17`的`powerful`镜像。 
    
    另外，`build.sh`脚本文件还支持以下命令行参数：
      - `-b`：使用已推送的operator-sdk镜像作为基础, 输出所有已支持的`springboot`和`JDK`版本的镜像；
      - `-cn`：不编译项目，基于已编译的Jar包输出镜像；
      - `-s`： 本地重新编译operator-sdk，并以最新的operator-sdk镜像为基础，输出基于`springboot 2.7`和`JDK17`的镜像；
      - `-sn`：使用本地已编译的operator-sdk镜像作为基础，输出基于`springboot 2.7`和`JDK17`的镜像；
      - `-in`：仅编译项目，不输出镜像。