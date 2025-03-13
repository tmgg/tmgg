FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-node as web
ADD web/package.json ./
RUN npm install
ADD web/ ./
RUN npm run build


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-java as java
ADD . .
RUN mvn clean  package -q  -DskipTests   &&    mv target/*.jar /app.jar && rm -rf *


# 阶段3 运行环境, 减少镜像大小
FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-jdk
WORKDIR /home
COPY --from=java /app.jar ./
COPY --from=web /build-node/dist/ ./static/
EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/app.jar"]
