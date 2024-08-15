// .umirc.local.ts 仅在 umi dev 时有效。umi build 时不会被加载
// 这份配置会和 .umirc.ts 做 deep merge 后形成最终配置。


import {defineConfig} from 'umi';

let target = 'http://127.0.0.1:88';

let proxyList = ['ureport', 'code', 'job', 'sso', 'flowable','weapp']

let proxy = {
  '/api': {
    target: target,
    changeOrigin: true,
    pathRewrite: {'^/api': '/'},
  },
};

for (let p of proxyList) {
  const cfg = {
    target: target,
    changeOrigin: true,
  }
  cfg.pathRewrite = {}
  cfg.pathRewrite['^/' + p] = '/' + p
  proxy['/' + p] = cfg
}


export default defineConfig({
  devServer: {
    port: 7701,
  },

  nodeModulesTransform: {
    type: 'none',
  },
  fastRefresh: {},
  define: {
    "process.env.API_BASE_URL": "/api/"
  },

  proxy
});
