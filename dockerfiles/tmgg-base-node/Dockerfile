FROM node:20
ARG VERSION=x.y.z

WORKDIR build

RUN npm install -g pnpm
RUN npm config set fund false
RUN npm config set registry https://registry.npmmirror.com
RUN pnpm config set registry https://registry.npmmirror.com

ADD web-monorepo/ ./
RUN pnpm -r exec pnpm version ${VERSION} && \
    cd web-monorepo/template/web/ && \
    pnpm install --force &&  \
    cd /build && rm -rf *




