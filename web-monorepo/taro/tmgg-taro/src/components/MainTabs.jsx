import React from "react";
import { Tabbar, TabbarItem } from '@antmjs/vantui'

import Taro from "@tarojs/taro";

export default class extends React.Component {

  onChange = e => {
    const v = e.detail
    let url = '/pages/main/'+v+'/index';
    Taro.redirectTo({url})
  }

  render() {
    const value = this.props.value;


    return <Tabbar active={value}
                   onChange={this.onChange}
                   style={{position: "fixed", bottom: 0, left: 0, right: 0}}>
      <TabbarItem name={'index'} icon='chat-o'>消息</TabbarItem>
      <TabbarItem name={'work'} icon="apps-o">工作</TabbarItem>
      <TabbarItem name={'mine'} icon="user-o">我的</TabbarItem>
    </Tabbar>
  }
}
