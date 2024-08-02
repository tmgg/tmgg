// .umirc.local.ts 仅在 umi dev 时有效。umi build 时不会被加载
// 这份配置会和 .umirc.ts 做 deep merge 后形成最终配置。


import {defineConfig} from 'umi';

let target = 'http://127.0.0.1:88';
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

  proxy: {
    '/api': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/api': '/' },
    },
    '/ureport': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/ureport': '/ureport' },
    },
    '/code': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/code': '/code' },
    },
    '/job': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/job': '/job' },
    },
    '/sso': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/sso': '/sso' },
    },
    '/flowable': {
      target: target,
      changeOrigin: true,
      pathRewrite: { '^/flowable': '/flowable' },
    },

  },
});
