import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../../components/MainTabs";
import {HttpUtil} from "@tmgg/tmgg-app-base";
import {Cell, CellGroup, Image} from "@antmjs/vantui";


export default class extends Component {

  state = {
    userInfo: {}
  }

  componentDidMount() {
    HttpUtil.get('/app/user/info').then(rs => {
      this.setState({userInfo: rs})
    })
  }

  render() {
    return <View style={{padding: 24, background: '#f5f5f5'}}>
      <View style={{background: '#fff', padding: 24}}>
        <view style={{display: 'flex', alignItems: 'center', gap: 24}}>
          <Image
            round
            width="60px"
            height="60px"
            src="https://img.yzcdn.cn/vant/cat.jpeg"
          />
          <View>
            <View>{this.state.userInfo.name}</View>
            <View>账号：{this.state.userInfo.account}</View>
          </View>
        </view>
      </View>

      <View style={{marginTop: 12}}>
        <Cell title='我的待办' border></Cell>
        <Cell title='消息提醒'></Cell>
      </View>

      <View style={{marginTop: 12}}>
        <Cell title='系统设置'></Cell>
        <Cell title='退出登录'></Cell>
      </View>

      <MainTabs value={'mine'}></MainTabs>
    </View>;

  }


}
