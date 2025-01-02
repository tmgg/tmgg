FROM registry.cn-hangzhou.aliyuncs.com/mxvc/maven:3-openjdk-17 as java
WORKDIR build-java
ADD . .
RUN mvn   package -DskipTests -q && rm -rf *



