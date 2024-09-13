import './app.scss'

import React from 'react'
import Taro from "@tarojs/taro";
import {SysUtil} from "web-monorepo/tmgg-commons-lang";
import {HttpUtil} from "@tmgg/tmgg-app-base";


export default class extends React.Component {

  onLaunch() {
    console.log('程序启动.')


    // TODO 检查登录


    // HttpUtil.get("/site-info").then(rs=>{
    //   SysUtil.setSiteInfo(rs)
    // })


  //  this.login()
  }

  onPageNotFound() {
    Taro.showToast({title: '页面不存在', icon: 'error'})
  }


  onError(error) {
    Taro.showModal({
      title: '系统错误',
      content: JSON.stringify(error),
      showCancel: false
    })
  }

  render() {
     return this.props.children
  }

  login = () => {
    Taro.showLoading({title: '登录中...'})
    Taro.login({}).then(res=> {
      let code = res.code;
      let appId = Taro.getAccountInfoSync().miniProgram.appId;
      console.log('本地登录成功, appId=', appId)
    })
  }


}
