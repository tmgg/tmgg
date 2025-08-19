FROM node:22
ARG VERSION=x.y.z

WORKDIR build

RUN npm install -g pnpm
RUN npm config set fund false
RUN npm config set registry https://registry.npmmirror.com
RUN pnpm config set registry https://registry.npmmirror.com






