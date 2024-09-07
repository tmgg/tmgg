import './app.scss'

import React from 'react'
import Taro from "@tarojs/taro";
import HttpClient from "./util/HttpClient";
import {setToken} from "./sys";
import UserUtil from "./util/UserUtil";
import {InitContext} from "./context/InitProvicer";

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
    return <InitContext.Provider value={this.state.initFinish}> {this.props.children}</InitContext.Provider>
  }

  login = () => {
    Taro.showLoading({title: '登录中...'})
    Taro.login({}).then(res=> {
      let code = res.code;
      let appId = Taro.getAccountInfoSync().miniProgram.appId;
      console.log('本地登录成功, appId=', appId)

      HttpClient.postForm('/app/weapp/login', {code, appId}).then(rs => {
        Taro.showToast({
          title: rs.message
        })
        if (!rs.success) {
          return
        }

        const {token, user} = rs.data;

        console.log('登录服务器成功', token)

        setToken(token)

        UserUtil._setUserInfo(user);
          this.setState({initFinish:true})

        Taro.hideLoading()
      })

    })
  }


}
