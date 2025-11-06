set  "target=D:\ws\wuzi\web\node_modules"



cd web-monorepo

call robocopy "packages/tmgg-commons-lang" "%target%/@tmgg/tmgg-commons-lang" /E /XD "node_modules"
call robocopy "backend/tmgg-base" "%target%/@tmgg/tmgg-base" /E /XD "node_modules"
call robocopy "backend/tmgg-system" "%target%/@tmgg/tmgg-system" /E /XD "node_modules"

call robocopy "backend-plugins/tmgg-system-flowable" "%target%/tmgg-system-flowable" /E /XD "node_modules"

call robocopy "backend-plugins/tmgg-system-ureport" "%target%/tmgg-system-ureport" /E /XD "node_modules"

