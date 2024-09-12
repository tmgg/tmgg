import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../components/MainTabs";
import {AuthPage} from "@tmgg/tmgg-app-base";


export default class extends Component {


  render() {
    return <AuthPage>
      <Text>首页</Text>

      <MainTabs value={'index'}></MainTabs>
    </AuthPage>;

  }


}
