FROM node:22
ARG VERSION=x.y.z

WORKDIR build

RUN npm install -g pnpm && \
    npm config set fund false && \
    npm config set registry https://registry.npmmirror.com && \
    pnpm config set registry https://registry.npmmirror.com


ADD ./web-monorepo/backend/tmgg-system/package.json ./

# 将workspace的版本调整为实际版本
RUN sed -i "s/workspace:\*/$VERSION/g" package.json
RUN cat package.json

RUN pnpm  install && rm -rf *

RUN pnpm store status



