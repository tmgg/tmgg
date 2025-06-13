# 作为项目构建的基础镜像maven， 包含依赖的库和本身的jar包
FROM maven:3-openjdk-17
ARG VERSION=x.y.z


WORKDIR build
ADD pom.xml ./
ADD template-backend ./template-backend
ADD tmgg-base ./tmgg-base
ADD tmgg-script ./tmgg-script
ADD tmgg-system ./tmgg-system
ADD tmgg-system-plugins ./tmgg-system-plugins


RUN mvn install -q  -Drevision=${VERSION}  && rm -rf *



