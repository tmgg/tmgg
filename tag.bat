@echo off
chcp 65001 > nul


set  "tag_name=v1.1.29"


echo 正在删除本地标签（如果存在）...
git tag -d %tag_name%

echo 正在删除远程标签（如果存在）...
git push origin --delete %tag_name%

echo 正在创建新标签...
git tag %tag_name%

echo 正在推送标签到远程仓库...
git push origin %tag_name%


echo 标签 %tag_name% 脚本执行完成
