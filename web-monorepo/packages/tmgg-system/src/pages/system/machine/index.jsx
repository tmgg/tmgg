import {Card, Col, Row} from 'antd';
import React, {Fragment} from 'react';


import './index.css'
import {http} from "@tmgg/tmgg-base";

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
      this.setState(rs.data)
    })
  }

  render() {

    const {loading, sysOsInfo, sysJavaInfo, sysJvmMemInfo} = this.state

    return <Fragment>
      <Row gutter={24}>
        <Col md={12} sm={24}>
          <Card loading={loading} title="系统信息" style={{marginBottom: 20}} bordered={false}>
            <table className="sysInfo_table">
              <tbody>
              <tr>
                <td className="sysInfo_td">系统名称：</td>
                <td className="sysInfo_td">{sysOsInfo.osName}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">系统架构：</td>
                <td className="sysInfo_td">{sysOsInfo.osArch}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">系统版本：</td>
                <td className="sysInfo_td">{sysOsInfo.osVersion}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">主机名称：</td>
                <td className="sysInfo_td">{sysOsInfo.osHostName}</td>
              </tr>
              <tr>
                <td>主机IP地址：</td>
                <td>{sysOsInfo.osHostAddress}</td>
              </tr>
              </tbody>
            </table>
          </Card>
        </Col>
        <Col md={12} sm={24}>
          <Card loading={loading} title="Java信息" style={{marginBottom: 20}}>
            <table className="sysInfo_table">
              <tbody>
              <tr>
                <td className="sysInfo_td">虚拟机名称：</td>
                <td className="sysInfo_td">{sysJavaInfo.jvmName}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">虚拟机版本：</td>
                <td className="sysInfo_td">{sysJavaInfo.jvmVersion}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">虚拟机供应商：</td>
                <td className="sysInfo_td">{sysJavaInfo.jvmVendor}</td>
              </tr>
              <tr>
                <td className="sysInfo_td">java名称：</td>
                <td className="sysInfo_td">{sysJavaInfo.javaName}</td>
              </tr>
              <tr>
                <td>java版本：</td>
                <td>{sysJavaInfo.javaVersion}</td>
              </tr>
              </tbody>
            </table>
          </Card>
        </Col>
      </Row>
      <Card loading={loading} title="JVM内存信息">
        <table className="sysInfo_table">
          <tbody>
          <tr>
            <td className="sysInfo_td">最大内存：</td>
            <td className="sysInfo_td">{sysJvmMemInfo.jvmMaxMemory}</td>
            <td className="sysInfo_td">可用内存：</td>
            <td className="sysInfo_td">{sysJvmMemInfo.jvmUsableMemory}</td>
          </tr>
          <tr>
            <td className="sysInfo_td">总内存：</td>
            <td className="sysInfo_td">{sysJvmMemInfo.jvmTotalMemory}</td>
            <td className="sysInfo_td">已使用内存：</td>
            <td className="sysInfo_td">{sysJvmMemInfo.jvmUsedMemory}</td>
          </tr>
          <tr className="sysInfo_tr">
            <td>空余内存：</td>
            <td>{sysJvmMemInfo.jvmFreeMemory}</td>
            <td>使用率：</td>
            <td>{sysJvmMemInfo.jvmMemoryUsedRate}</td>
          </tr>
          </tbody>
        </table>
      </Card>

    </Fragment>
  }


}



