@echo off

set new_version=0.1.4

echo current dir is
cd

call mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DgenerateBackupPoms=false -DnewVersion=%new_version%



cd web-monorepo
call npm version  %new_version% --allow-same-version


cd /d %~dp0
cd web-monorepo/packages/tmgg-tools
call npm version  %new_version% --allow-same-version


cd /d %~dp0
cd web-monorepo/packages/tmgg-components
call npm version  %new_version% --allow-same-version


cd /d %~dp0
cd web-monorepo/apps/tmgg-system
call npm version  %new_version% --allow-same-version


cd /d %~dp0
cd web-monorepo/apps/tmgg-system-job
call npm version  %new_version% --allow-same-version
