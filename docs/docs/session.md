---
title: session
layout: default
---

# 使用spring session， 并持久化到硬盘





# 支持rest api
登录后spring 会自动增加响应头 X-Auth-Token。这个和sessionId一样。请求时带上X-Auth-Token即可

# 为什么使用session认证
为了集成一些第三方功能页面，如 ureport。 使用session后，不用再考虑集成认证。
