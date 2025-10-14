@echo off

:: 参数
set  "tag_name=1.1.58-SNAPSHOT"

echo %TIME%  %tag_name% start

call mvnw  clean  deploy    -DskipTests   -P snapshot -Drevision=%tag_name%

echo %TIME%  %tag_name% finish





