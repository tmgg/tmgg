@echo off
chcp 65001 > nul


set  "tag_name=v2.0.0"


git tag -d %tag_name%

git push origin --delete %tag_name%

git tag %tag_name%

git push origin %tag_name%


echo  %tag_name% finished
