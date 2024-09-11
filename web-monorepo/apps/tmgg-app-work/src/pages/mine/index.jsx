import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../components/MainTabs";


export default class extends Component {


  render() {
    return <View>
      <Text>我的</Text>

      <MainTabs value={'mine'}></MainTabs>
    </View>;

  }


}
