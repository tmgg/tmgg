# 作为项目构建的基础镜像maven， 包含依赖的库和本身的jar包
FROM maven:3-openjdk-17
ARG VERSION=x.y.z

WORKDIR build

ADD . .

RUN mvn install package -q -DskipTests  -Drevision=${VERSION}  && rm -rf *


RUN ls /root/.m2/
RUN ls /root/.m2/repository/io/github/tmgg
