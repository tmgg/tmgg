@echo off

:: 参数
set  "tag_name=%~1"

echo %TIME%  %tag_name% start

call mvnw  clean  install -o   -DskipTests -T 1C  -Drevision=%tag_name%

echo %TIME%  %tag_name% finish





