import {defineConfig} from 'umi';



// @ts-ignore
export default defineConfig({
  npmClient: 'yarn',

  history: {
    type: 'hash'
  },
  define: {
    "process.env.API_BASE_URL": "/",
    "process.env.LOGIN_URL": "/login",
    "process.env.TITLE": "管理后台",
  },



  /**
   * 配置是否让生成的文件包含 hash 后缀，通常用于增量发布和避免浏览器加载缓存。
   */
  hash: true,

  routes: [
    { path: "/login", component: "login" ,layout:false},
    {
      path: '/',
      component: '@/layouts/index',
      routes: [
        { path: "/", component: "index" },
      ],
    },



  ],


});
