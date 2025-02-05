// .umirc.local.ts 仅在 umi dev 时有效。umi build 时不会被加载
// 这份配置会和 .umirc.ts 做 deep merge 后形成最终配置。


import {defineConfig} from 'umi';

let target = 'http://127.0.0.1:8002';


let proxy = {
  '/api': {
    target: target,
    changeOrigin: true,
    pathRewrite: {'^/api': '/'},
  },
  '/ureport': {
    target: target,
    changeOrigin: true,
    pathRewrite: {'^/ureport': '/ureport'},
  },
};

export const configLocal = {


  define: {
    "process.env.API_BASE_URL": "/api/"
  },

  proxy
};
export default defineConfig(configLocal);
