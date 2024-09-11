import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../components/MainTabs";


export default class extends Component {


  render() {
    return <View>
      <Text>工作</Text>

      <MainTabs value='work'></MainTabs>
    </View>;

  }


}
