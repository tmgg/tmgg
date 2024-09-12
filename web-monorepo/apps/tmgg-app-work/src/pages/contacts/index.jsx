import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../components/MainTabs";
import {Cell, List} from "@taroify/core";


export default class extends Component {


  render() {
    return <View>
      <Text>通讯录</Text>
      <List>
        <Cell>张三</Cell>
        <Cell>李四</Cell>
      </List>

      <MainTabs value={'contacts'}></MainTabs>
    </View>;

  }


}
