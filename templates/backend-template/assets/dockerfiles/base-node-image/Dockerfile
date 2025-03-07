FROM node:18 as web

WORKDIR build-node



ADD web/package.json .
RUN npm install --force && rm package.*

RUN npm config set registry https://registry.npmmirror.com/
