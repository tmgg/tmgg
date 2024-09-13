import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../components/MainTabs";
import {HttpUtil} from "@tmgg/tmgg-app-base";


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
    return <View>
      <Text>我的</Text>

      账号：

      {JSON.stringify(this.state.userInfo)}

      <MainTabs value={'mine'}></MainTabs>
    </View>;

  }


}
