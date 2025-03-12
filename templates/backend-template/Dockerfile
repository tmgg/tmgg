FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-node as web
ADD web/ ./
RUN npm install
RUN npm run build


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-java as java
ADD . .
COPY --from=web /build-node/dist/ src/main/resources/static/
RUN mvn clean  package -q  -DskipTests   &&    mv target/*.jar /app.jar && rm -rf *


# 阶段3 运行环境, 减少镜像大小
FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-jdk

COPY --from=java /home/app.jar /home/app.jar
EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/home/app.jar"]
