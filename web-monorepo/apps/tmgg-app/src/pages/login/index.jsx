import {View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";


export default class extends Component {

  state = {
    formOpen:false,
    userTheme:"normal-theme"
  }

  render() {
    return <View>登录</View>;
  }

}
