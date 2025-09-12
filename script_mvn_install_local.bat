@echo off

set  "tag_name=1.1.38"

echo %TIME%  %tag_name% start

call ./mvnw  clean  install -o  -q -DskipTests -T 1C  -Drevision=%tag_name%

echo %TIME%  %tag_name% finish





