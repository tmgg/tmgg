import React from "react";
import {Card, Tabs} from "antd";
import ChangePassword from "./ChangePassword";
import MsgSubscribe from "./MsgSubscribe";

export default class extends React.Component {


  render() {

    return <div >
      <Card title='个人设置'>
      <Tabs
        items={[
          {label: '修改密码', key: 'pwd', children: <div>
              <ChangePassword />

            </div>},
          {label: '消息订阅', key: 'topic', children: <div><MsgSubscribe /></div>}
        ]}
        tabPosition='left'
      >
      </Tabs></Card>
    </div>

  }
}
