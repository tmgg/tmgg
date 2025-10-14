@echo off

:: 参数
set  "tag_name=0.1.58"

echo %TIME%  %tag_name% start

::call mvnw  clean  deploy    -DskipTests   -P snapshot -Drevision=%tag_name%-SNAPSHOT

pnpm -r exec pnpm version %tag_name%
pnpm publish -r --access public --no-git-checks  --registry http://10.79.43.231:8081/repository/npm/

echo %TIME%  %tag_name% finish





