@echo off
set  "tag_name=1.1.30"


echo install mvn jar to local, version is %tag_name%
./mvnw clean  install   -Drevision=%tag_name%


echo  %tag_name% finished
