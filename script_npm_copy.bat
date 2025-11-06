set  "target=D:\ws\wuzi\web\node_modules"



cd web-monorepo

call robocopy "packages/tmgg-commons-lang" "%target%/@tmgg/tmgg-commons-lang" /E
call robocopy "backend/tmgg-base" "%target%/@tmgg/tmgg-base" /E
call robocopy "backend/tmgg-system" "%target%/@tmgg/tmgg-system" /E



