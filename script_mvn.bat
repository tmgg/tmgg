@echo off

:: 参数

echo %TIME%  %tag_name% start

call mvnw  clean  install -o -q  -DskipTests -T 1C

echo %TIME%  %tag_name% finish

pause
