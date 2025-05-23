chcp 65001

@echo off
echo 将代码部署到本机，以便集成测试

echo 执行 npm
cd cd web-monorepo
call pnpm publish -r --access public --no-git-checks --registry https://packages.aliyun.com/62d39be70065edd3d51c1984/npm/npm-registry/
cd ..


::call mvn install -Drevision=1.1.1 -DskipTests  -B -T 1C


echo -------------------------------------------------------------------
echo -------------------------------------------------------------------

