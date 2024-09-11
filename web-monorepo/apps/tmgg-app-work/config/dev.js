export default {
   logger: {
    quiet: false,
    stats: true
  },
  mini: {},
  h5: {
    devServer: {
      proxy: {
        '/api': {
          target: 'http://127.0.0.1:88', // 后端地址
          changeOrigin: true,
          pathRewrite: {
            '^/api': ''
          }
        }
      }
    }
  }
}
