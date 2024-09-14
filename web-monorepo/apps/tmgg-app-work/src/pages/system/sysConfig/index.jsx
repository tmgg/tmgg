import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import {HttpUtil} from "@tmgg/tmgg-app-base";


export default class extends Component {

  componentDidMount() {
    HttpUtil.pageData('sysConfig/page').then(rs=>{
      debugger
    })
  }


  render() {
    return <View>

    </View>;

  }


}
