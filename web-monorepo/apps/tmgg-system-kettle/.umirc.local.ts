// .umirc.local.ts 仅在 umi dev 时有效。umi build 时不会被加载
// 这份配置会和 .umirc.ts 做 deep merge 后形成最终配置。


import {defineConfig} from 'umi';

let proxyTarget = 'http://127.0.0.1:88';
export default defineConfig({

  devServer: {
    port: 6003,
  },


  proxy: {
    '/kettle': {
      target: proxyTarget,
      changeOrigin: true,
      pathRewrite: { '^/kettle': '/kettle' },
    },
  },
});
