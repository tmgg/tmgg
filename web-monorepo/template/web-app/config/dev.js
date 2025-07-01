

export default {
   logger: {
    quiet: false,
    stats: true
  },
  mini: {},
  h5: {
     // 防止一遇到异常就弹窗异常窗口
    devServer:{
      client:{
        overlay:false
      }
    }
  }
}
