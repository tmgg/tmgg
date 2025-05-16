import React from "react";
import {Card, Table, Tabs} from "antd";
import MyInstance from "../../../components/MyInstance";
import DoneTask from "../../../components/doneTask";
import TodoTask from "../../../components/todoTask";
import {Page, PageLoading} from "@tmgg/tmgg-base";


export default class extends React.Component {

  state = {
    show: true
  }

  render() {
    if(!this.state.show){
      return  <PageLoading />
    }

    const items = [
      {label: '待办任务', key: '1', children: <TodoTask />},
      {label: '已办任务', key: '2', children: <DoneTask />},
      {label: '我发起的', key: '3', children: <MyInstance />},
    ]

    return <Page padding>
      <Tabs defaultActiveKey="1" destroyOnHidden items={items}>

      </Tabs>
    </Page>
  }
}
