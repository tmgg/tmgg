import { View } from '@tarojs/components'
import { Button } from '@antmjs/vantui'
import './index.less'
import React from "react";
import Taro from "@tarojs/taro";
import {HttpUtil} from "@tmgg/tmgg-taro";



export default class Login extends React.Component {

  state ={
    userInfo:{}
  }

  componentDidMount () { }

  componentWillUnmount () { }

  componentDidShow () { }

  componentDidHide () { }


  onLoginClick = () => {
    Taro.login({}).then(res=> {
      let code = res.code;

      HttpUtil.postForm('/app/weixin/mini/login', {code}).then(rs => {
        if (!rs.success) {
          return
        }

        Taro.showToast({title:'登录成功'})
      })})
  };

  loadUserInfo = () => {

      HttpUtil.get('/app/weixin/mini/getUserInfo').then(rs => {
        this.setState({userInfo:rs.data})
      })

  };

  render () {
    return (
      <View className='index'>
        <View>
      <Button type='primary' onClick={this.onLoginClick}>点击微信登录</Button>
        </View>     <View>
        <Button type='primary' onClick={this.loadUserInfo}>测试加载用户信息</Button>
      </View>
        用户信息：{JSON.stringify(this.state.userInfo)}
      </View>
    )
  }
}
