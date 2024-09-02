import React from 'react'
import {Card, Descriptions, Tabs} from "antd";
import KettleFile from "../components/KettleFile";
import {http, theme} from "@tmgg/tmgg-base";
import JobList from "../components/JobStatus";
import SysJob from "../components/SysJob";

const Item = Descriptions.Item

import '../init'

export default class extends React.Component {

  state = {
    status: {}
  }

  componentDidMount() {
    HttpUtil.get('/kettle/status').then(rs => {
      this.setState({status: rs.data || {}})
    })
  }

  render() {
    const status = this.state.status
    const iframe = window.self != window.top;
    return <div style={{padding: iframe ? 0: 12, minHeight:'98vh', background: theme["background-color"]}}>
      <div>
        <Card>
          <Descriptions title='Kettle服务状态'>
            <Item label='服务状态'>{status.statusDescription}</Item>
            <Item label='空闲内存'>{status.memoryFree}</Item>
            <Item label='内存'>{status.memoryTotal}</Item>
            <Item label='cpu核心'>{status.cpuCores}</Item>
            <Item label='cpu处理时间'>{status.cpuProcessTime}</Item>
            <Item label='启动时间'>{status.uptime}</Item>
            <Item label='线程数量'>{status.threadCount}</Item>
            <Item label='loadAvg'>{status.loadAvg}</Item>
            <Item label='系统名称'>{status.osName}</Item>
            <Item label='系统版本'>{status.osVersion}</Item>
            <Item label='系统架构'>{status.osArchitecture}</Item>

          </Descriptions></Card>

        <Card style={{marginTop: 16}}>


          <Tabs items={[
            {
              label: '监控',
              key: 'job_monitor',
              children: <JobList/>,
            },
            {
              label: '文件',
              key: 'file',
              children: <KettleFile/>,
            },
            {
              label: '调度',
              key: 'job_scheduling',
              children: <SysJob />,
            },

          ]}
                destroyInactiveTabPane
          >

          </Tabs>

        </Card>
      </div>
    </div>
  }


}



