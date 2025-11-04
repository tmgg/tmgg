cd web-monorepo


call pnpm -r exec pnpm version prerelease --preid=beta
call pnpm publish -r --access public --no-git-checks --tag beta --registry http://10.79.43.231:8081/repository/npm/






