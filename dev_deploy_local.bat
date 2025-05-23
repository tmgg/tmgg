chcp 65001

@echo off
echo 将代码部署到本机，以便集成测试

echo 正在执行 mvn
call mvn install -Drevision=1.1.1
echo '执行 mvn 完毕'


echo -------------------------------------------------------------------
echo
echo 执行pnpm
