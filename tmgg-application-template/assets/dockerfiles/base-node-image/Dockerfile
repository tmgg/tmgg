# 利用pnpm 的缓存机制，建立基础镜像

FROM node:18 as web

WORKDIR build-node

ADD web/package.json .
RUN npm install --force

RUN npm config set fund false
RUN npm config set registry https://registry.npmmirror.com
