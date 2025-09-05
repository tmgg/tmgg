FROM node:22
ARG VERSION=x.y.z

WORKDIR build

RUN npm install -g pnpm && \
    npm config set fund false && \
    npm config set registry https://registry.npmmirror.com && \
    pnpm config set registry https://registry.npmmirror.com

RUN pnpm store path

ADD ./web-monorepo/backend/tmgg-system/package.json ./
RUN sed -i "s/workspace:\*/$VERSION/g" package.json
RUN cat package.json

RUN pnpm  install






