import React from "react";
import {Card, Tabs} from "antd";
import AllTask from "../../components/monitor/AllTask";
import AllDefinition from "../../components/monitor/AllDefinition";
import AllInstance from "../../components/monitor/AllInstance";

export default class extends React.Component {

  render() {
    const items = [
      { label: '所有流程', key: 'AllInstance', children: <AllInstance /> },
      { label: '所有任务', key: 'AllTask', children: <AllTask /> },
      { label: '所有定义', key: 'AllDefinition', children: <AllDefinition /> },
    ];
    return <Card> <Tabs items={items} destroyInactiveTabPane /></Card>

  }
}
