FROM node:18 as web

WORKDIR build-node

RUN npm config set registry https://registry.npmmirror.com/

ADD web/package.json .
RUN npm install --no-fund

