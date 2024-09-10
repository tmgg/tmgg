import './app.scss'

import React from 'react'
import Taro from "@tarojs/taro";
import {AppAuthInterceptor, AppLoginInfoInterceptor, AppSiteInfoInterceptor} from "@tmgg/tmgg-app-base";


export default class extends React.Component {
  state = {
    initFinish:false
  }

  onLaunch() {
    console.log('程序启动.')

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
      if (this.state.pathname === '/login') {
      return <AppSiteInfoInterceptor>
        {this.props.children}
      </AppSiteInfoInterceptor>
    }

    return <>
      <AppSiteInfoInterceptor>
        <AppAuthInterceptor>
          <AppLoginInfoInterceptor>
            {this.props.children}
          </AppLoginInfoInterceptor>
        </AppAuthInterceptor>
      </AppSiteInfoInterceptor>
    </>
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
