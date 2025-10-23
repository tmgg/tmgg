chcp 65001
@echo off

:: 参数



cd web-monorepo

:: 设置为下个版本 pnpm -r exec pnpm version 1.1.60-beta.0

call pnpm -r exec pnpm version prerelease --preid=beta
call pnpm publish -r --access public --no-git-checks --tag beta --registry http://10.79.43.231:8081/repository/npm/






