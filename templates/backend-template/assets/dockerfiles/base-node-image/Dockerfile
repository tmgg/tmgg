# 利用pnpm 的缓存机制，建立基础镜像

FROM node:18 as web

RUN npm install pnpm -g


ADD web/package.json .
RUN pnpm install --force && rm *

RUN npm  config set registry https://registry.npmmirror.com
RUN pnpm config set registry https://registry.npmmirror.com
