import React from "react";
import {Tabbar} from "@taroify/core";
import {AppsOutlined, ChatOutlined, NewspaperOutlined, UserOutlined} from "@taroify/icons";
import Taro from "@tarojs/taro";

export default class extends React.Component {

  onChange = v => {
    let url = '/pages/'+v+'/index';
    Taro.redirectTo({url})
  }

  render() {
    const value = this.props.value;


    return <Tabbar defaultValue={value}
                   onChange={this.onChange}
                   style={{position: "fixed", bottom: 0, left: 0, right: 0}}>
      <Tabbar.TabItem value={'index'} icon={<ChatOutlined/>}>消息</Tabbar.TabItem>
      <Tabbar.TabItem value={'work'} icon={<AppsOutlined/>}>工作</Tabbar.TabItem>
      <Tabbar.TabItem value={'contacts'} icon={<NewspaperOutlined/>}>通讯录</Tabbar.TabItem>
      <Tabbar.TabItem value={'mine'} icon={<UserOutlined/>}>我的</Tabbar.TabItem>
    </Tabbar>
  }
}
