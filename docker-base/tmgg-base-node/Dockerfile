FROM node:22
ARG VERSION=x.y.z

WORKDIR build

RUN npm install -g pnpm
RUN npm config set fund false
RUN npm config set registry https://registry.npmmirror.com
RUN pnpm config set registry https://registry.npmmirror.com


ADD ./web-monorepo/backend/tmgg-system/package.json ./
RUN sed -i "s/workspace:\*/$VERSION/g" package.json
RUN cat package.json

RUN pnpm  install

RUN ls ~/.pnpm-store/



