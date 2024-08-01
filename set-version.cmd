@echo off

set new_version=0.1.6

echo current dir is
cd

call mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DgenerateBackupPoms=false -DnewVersion=%new_version%



cd web-monorepo
call lerna version %new_version% --no-git-tag-version --no-push --yes




