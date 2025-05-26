FROM maven:3-openjdk-17
WORKDIR build-java
ADD . .
RUN mvn package -DskipTests -q && rm -rf *



