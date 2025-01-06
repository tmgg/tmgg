import {Card, Col, Row, Descriptions, Space} from 'antd';
import React, {Fragment} from 'react';


import {HttpUtil} from "@tmgg/tmgg-base";

const {Item} = Descriptions


export default class extends React.Component {

  state = {
    loading: true,
    sysOsInfo: [],
    sysJavaInfo: [],
    sysJvmMemInfo: []

  }

  componentDidMount() {
    this.setState({loading: true})
    HttpUtil.get('sysMachine/query').then(rs => {
      this.setState({loading: false})
      this.setState(rs)
    })
  }

  render() {

    const {loading, sysOsInfo, sysJavaInfo, sysJvmMemInfo} = this.state

    return <Space size={12} direction='vertical'>
          <Card loading={loading}  >
            <Descriptions column={2} title='系统信息' labelStyle={{width:'200px'}} colon={false}>
              <Item label='系统名称'>{sysOsInfo.osName}</Item>
              <Item label='系统架构'>{sysOsInfo.osArch}</Item>
              <Item label='系统版本'>{sysOsInfo.osVersion}</Item>
              <Item label='主机名称'>{sysOsInfo.osHostName}</Item>
              <Item label='主机IP地址'>{sysOsInfo.osHostAddress}</Item>
            </Descriptions>

          </Card>
          <Card loading={loading} >
            <Descriptions column={2} title='Java信息' labelStyle={{width:'200px'}} colon={false}>
              <Item label='虚拟机名称'>{sysJavaInfo.jvmName}</Item>
              <Item label='虚拟机版本'>{sysJavaInfo.jvmVersion}</Item>
              <Item label='虚拟机供应商'>{sysJavaInfo.jvmVendor}</Item>
              <Item label='java名称'>{sysJavaInfo.javaName}</Item>
              <Item label='java版本'>{sysJavaInfo.javaVersion}</Item>
            </Descriptions>
          </Card>

          <Card loading={loading}>
            <Descriptions column={1} title='JVM内存信息' labelStyle={{width:'200px'}} colon={false}>
              <Item label='最大内存'>{sysJvmMemInfo.jvmMaxMemory}</Item>
              <Item label='可用内存'>{sysJvmMemInfo.jvmUsableMemory}</Item>
              <Item label='总内存'>{sysJvmMemInfo.jvmTotalMemory}</Item>
              <Item label='已使用内存'>{sysJvmMemInfo.jvmUsedMemory}</Item>
              <Item label='空余内存'>{sysJvmMemInfo.jvmFreeMemory}</Item>
              <Item label='使用率'>{sysJvmMemInfo.jvmMemoryUsedRate}</Item>
            </Descriptions>


          </Card>


    </Space>
  }


}



