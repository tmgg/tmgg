import {Text, View} from '@tarojs/components'
import {Button, Input, Popup} from "@taroify/core"

import './index.scss'
import React, {Component} from "react";
import Taro from "@tarojs/taro";
import HttpClient from "../../util/HttpClient";
import UserForm from "../../components/UserForm";
import UserUtil from "../../util/UserUtil";
import {InitContext} from "../../context/InitProvicer";

export default class extends Component {

  state = {
    formOpen:false,
    userTheme:"normal-theme"
  }

  onClickPhone = (e) => {
    const {code} = e.detail


    // 解密用户并缓存
    HttpClient.postForm("/app/weapp/decryptPhone", {code}).then(rs => {
      Taro.showToast({
        title: rs.data
      })
    })
  }



  onNickNameUpdate = e=>{
    HttpClient.postForm("/app/weapp/updateUserNickName", {e}).then(rs => {
      Taro.showToast({
        title: e
      })
    })
  }

  // 先判断是否全局初始化完成再渲染页面
  render(){
    return <InitContext.Consumer>{(initFinish)=>{
      return initFinish ? this.renderPage() : <View>Loading.... </View>
    }}</InitContext.Consumer>
  }

  renderPage() {
    return <View className={UserUtil.getUserTheme()}>

      <Text>Hello world1133335d</Text>

      <View style={{display: 'flex', flexDirection: 'column', gap: 10}}>
        <Button openType='getPhoneNumber' onGetPhoneNumber={this.onClickPhone} color='primary'>获取手机号</Button>


        <Button color='primary' onClick={() => {
          this.setState({formOpen: true})
        }}>弹出用户表单</Button>


        {this.getView()}
      </View>

      用户信息：
      {JSON.stringify(UserUtil.getUserInfo())}

      <Popup open={this.state.formOpen} rounded placement="bottom" style={{height: "50%"}} onClose={
        () => {
          this.setState({formOpen: false})
        }
      }>
        <UserForm/>
      </Popup>

    </View>;

  }

  getView() {
    return <>
      <Button color="info" className="app-font-size-m" onClick={()=>UserUtil.updateUserTheme('NORMAL').then(res=>{this.setState({userTheme:'normal-theme'})})}>正常模式</Button>
      <Button color="success" className="app-font-size-l" onClick={()=>UserUtil.updateUserTheme('CARE').then(res=>{this.setState({userTheme:'care-theme'})})}>关怀模式</Button>
      <Button color="warning" className="app-font-size-xl">警告按钮</Button>
      <Button color="danger" className="app-font-size-xxl">危险按钮</Button>
      <Button color="default" className="app-font-size-xxxl">默认按钮</Button>
    </>;
  }

}
