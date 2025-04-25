import {View, Text, Button} from '@tarojs/components'
import './index.less'
import {HttpUtil, SysUtil} from "@tmgg/tmgg-app-base";
import {Component} from "react";
import Taro from "@tarojs/taro";

export default class Index extends Component {


  ping(){
    HttpUtil.get('/app-api/ping').then(res=>{
      console.log('正常',res)
    }).catch(e => {
      console.log('异常', e)
    })
  }
  login(){
    HttpUtil.postForm('/app-api/login',{username:'test', password:'China@520'}).then(res=>{
      console.log('正常',res)
      const {expireTime,token} = res.data
      SysUtil.setToken(token, expireTime)

    }).catch(e => {
      console.log('异常', e)
    })
  }
  loginByWeixin(){
    Taro.login({
      success: function (res) {
        if (res.code) {
          HttpUtil.postForm('/app-api/loginByWeixin',{code:res.code}).then(res=>{
            console.log('正常',res)
            const {expireTime,token} = res.data
            SysUtil.setToken(token, expireTime)

          }).catch(e => {
            console.log('异常', e)
          })
        } else {
          console.log('登录失败！' + res.errMsg)
        }
      }
    })
  }


  checkToken(){
    const ok = SysUtil.isTokenValid()
    console.log('是否合法', ok)
  }

  getStep(){
    Taro.getWeRunData({
      success(res){
        const {encryptedData,          errMsg,        iv} = res;
        HttpUtil.postForm('/app-api/getWeRunData',{encryptedData, iv})
      }

    })
  }

  render() {
    return (
      <View className='index'>123455
        <Button onClick={this.ping}>ping</Button>
        <Button onClick={this.login}>login</Button>
        <Button onClick={this.loginByWeixin}>loginByWeixin</Button>
        <Button onClick={this.checkToken}>checkToken</Button>
        <Button onClick={this.getStep}>获取步数</Button>
      </View>
    )
  }
}
