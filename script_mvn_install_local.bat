@echo off
set  "tag_name=1.1.39"


echo install mvn jar to local, version is %tag_name%
./mvnw clean  install  -DskipTests -Drevision=%tag_name%


echo  %tag_name% finished
