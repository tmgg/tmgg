FROM node:20

WORKDIR build-node

ADD web/package.json .
RUN npm install --force

RUN npm config set fund false
RUN npm config set registry https://registry.npmmirror.com
