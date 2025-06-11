# 作为项目构建的基础镜像maven， 包含依赖的库和本身的jar包
FROM maven:3-openjdk-17
ARG VERSION=x.y.z


WORKDIR build
ADD pom.xml ./
ADD template-backend ./template-backend
ADD tmgg-base ./tmgg-base
ADD tmgg-script ./tmgg-script
ADD tmgg-system ./tmgg-system
ADD tmgg-system-flowable ./tmgg-system-flowable
ADD tmgg-system-kettle ./tmgg-system-kettle
ADD tmgg-system-payment ./tmgg-system-payment
ADD tmgg-system-ureport ./tmgg-system-ureport
ADD tmgg-system-weixin ./tmgg-system-weixin

RUN echo "VERSION ARG is: ${VERSION}"
RUN mvn install -q  -Drevision=${VERSION}  && rm -rf *



