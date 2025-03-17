export default defineAppConfig({
  tabBar:{
    list: [
      {text:'首页',pagePath: 'pages/index/index'},
      {text:'我的',pagePath: 'pages/login/index'},
    ]
  },

  pages: [
    'pages/index/index',
    'pages/login/index',
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#fff',
    navigationBarTitleText: 'WeChat',
    navigationBarTextStyle: 'black'
  },
  animation: false,


}
)
