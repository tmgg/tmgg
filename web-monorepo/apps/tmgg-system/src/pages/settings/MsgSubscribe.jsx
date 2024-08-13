import React from "react";
import {arr, http} from "@tmgg/tmgg-base";
import {List, message, Switch} from "antd";

export default class extends React.Component {

  state = {
    topicList: [],
    myTopicList: []
  }

  componentDidMount() {
    http.get('/sysMsgSubscribe/myTopicList').then(rs => {
      this.setState({myTopicList: rs.data})
    })
    http.get("/sysMsgTopic/list").then(rs => {
      this.setState({topicList: rs.data})
    })
  }

  toggleSub = (v, topic) => {
    const {myTopicList} = this.state
    if (v) {
      myTopicList.push(topic)
      http.get('/sysMsgSubscribe/subscribe', {topic}).then(rs => {
        message.success(rs.message)
      })
    } else {
      arr.remove(myTopicList, topic)
      http.get('/sysMsgSubscribe/unsubscribe', {topic}).then(rs => {
        message.success(rs.message)
      })
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
              checked={arr.contains(this.state.myTopicList, item.code)}
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
