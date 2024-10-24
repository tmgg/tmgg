import React from "react";
import { HttpUtil} from "@tmgg/tmgg-base";
import {List, message, Switch} from "antd";
import {ArrUtil} from "@tmgg/tmgg-commons-lang";

export default class extends React.Component {

  state = {
    topicList: [],
    myTopicList: []
  }

  componentDidMount() {
    HttpUtil.get('/sysMsgSubscribe/myTopicList').then(rs => {
      this.setState({myTopicList: rs})
    })
    HttpUtil.get("/sysMsgTopic/list").then(rs => {
      this.setState({topicList: rs})
    })
  }

  toggleSub = (v, topic) => {
    const {myTopicList} = this.state
    if (v) {
      myTopicList.push(topic)
      HttpUtil.get('/sysMsgSubscribe/subscribe', {topic})
    } else {
      ArrUtil.remove(myTopicList, topic)
      HttpUtil.get('/sysMsgSubscribe/unsubscribe', {topic})
    }
    this.setState({myTopicList})
  }

  render() {
    return <div>

      <List
        bordered
        dataSource={this.state.topicList}
        renderItem={(item) => (
          <List.Item actions={[
            <Switch
              checked={ArrUtil.contains(this.state.myTopicList, item.code)}
              onChange={(v) => this.toggleSub(v, item.code)}
            />]}

          >
            <List.Item.Meta title={item.code} description={item.description}
            >

            </List.Item.Meta>

          </List.Item>
        )}
      />


    </div>
  }
}
