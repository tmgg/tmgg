import {Card, Col, Row} from 'antd';
import React from 'react';


import {HttpUtil, Page} from "@tmgg/tmgg-base";

function Usage(props) {
    const {value} = props;
    return <div style={{color: value > 70 ? 'red' : 'inherit'}}>
        {value} %
    </div>
}

export default class extends React.Component {

    state = {

        cpuInfo: {
            cpuNum: 0,
            sys: 0,
            user: 0,
            free: 0,
            used: 0
        },
        memInfo: {},
        jvmMemInfo: {},
        osInfo: {},
        jvmInfo: {},
        disks: []
    }


    componentDidMount() {


        HttpUtil.get('sysMachine/cpu').then(rs => {
            this.setState({cpuInfo: rs})
        })

        HttpUtil.get('sysMachine/mem').then(rs => {
            this.setState({memInfo: rs})
        })
        HttpUtil.get('sysMachine/jvmMem').then(rs => {
            this.setState({jvmMemInfo: rs})
        })

        HttpUtil.get('sysMachine/osInfo').then(rs => {
            this.setState({osInfo: rs})
        })
        HttpUtil.get('sysMachine/jvmInfo').then(rs => {
            this.setState({jvmInfo: rs})
        })
        HttpUtil.get('sysMachine/disks').then(rs => {
            this.setState({disks: rs})
        })
    }


    render() {

        const {loading, jvmInfo, disks, osInfo} = this.state

        return <Page padding>

            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <Card title='CPU'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th>
                                    属性
                                </th>
                                <th>
                                    值
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    核心数
                                </td>
                                <td>
                                    {this.state.cpuInfo.cpuNum}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    使用率
                                </td>
                                <td>
                                    <Usage value={this.state.cpuInfo.used}/>
                                </td>
                            </tr>
                            </tbody>
                        </table>

                    </Card>
                </Col>
                <Col span={12}>
                    <Card title='内存'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th>属性</th>
                                <th>内存</th>
                                <th>JVM</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>总内存</td>
                                <td>{this.state.memInfo.total}</td>
                                <td>{this.state.jvmMemInfo.total}</td>
                            </tr>
                            <tr>
                                <td>已用内存</td>
                                <td>{this.state.memInfo.used}</td>
                                <td>{this.state.jvmMemInfo.used}</td>
                            </tr>
                            <tr>
                                <td>剩余内存</td>
                                <td>{this.state.memInfo.free}</td>
                                <td>{this.state.jvmMemInfo.free}</td>
                            </tr>
                            <tr>
                                <td>使用率</td>
                                <td>
                                    <Usage value={this.state.memInfo.usage}/>
                                </td>
                                <td>

                                    <Usage value={this.state.jvmMemInfo.usage}/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </Card>
                </Col>

                <Col span={12}>
                    <Card title='服务器信息'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th>属性</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>操作系统</td>
                                <td>{osInfo.name}</td>
                            </tr>
                            <tr>
                                <td>系统架构</td>
                                <td>{osInfo.arch}</td>
                            </tr>
                            <tr>
                                <td>系统版本</td>
                                <td>{osInfo.version}</td>
                            </tr>


                            <tr>
                                <td>文件分隔符</td>
                                <td>{osInfo.fileSeparator}</td>
                            </tr>
                            <tr>
                                <td>换行分隔符</td>
                                <td> {osInfo.lineSeparator?.replace(/\r/g, "\\r").replace(/\n/g, "\\n")}</td>
                            </tr>
                            <tr>
                                <td>主机IP</td>
                                <td>{osInfo.hostAddress}</td>
                            </tr>
                            </tbody>
                        </table>


                    </Card>
                </Col>

                <Col span={12}>
                    <Card title='Java虚拟机信息'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th>属性</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    Java名称
                                </td>
                                <td>
                                    {jvmInfo.name}
                                </td>

                            </tr>
                            <tr>
                                <td>
                                    Java版本
                                </td>
                                <td>
                                    {jvmInfo.version}
                                </td>
                            </tr>
                            <tr>
                                <td>品牌</td>
                                <td>{jvmInfo.vendor}</td>
                            </tr>
                            <tr>

                                <td>
                                    启动时间
                                </td>
                                <td>
                                    {jvmInfo.startTime}
                                </td>

                            </tr>
                            <tr>
                                <td>
                                    运行时长
                                </td>
                                <td>
                                    {jvmInfo.pastTime}
                                </td>
                            </tr>
                            <tr>
                                <td >
                                    安装路径
                                </td>
                                <td >
                                    {jvmInfo.home}
                                </td>
                            </tr>
                            <tr>
                                <td >
                                    项目路径
                                </td>
                                <td >
                                    {jvmInfo.userDir}
                                </td>
                            </tr>
                            </tbody>
                        </table>

                    </Card>

                </Col>


                <Col span={24}>
                    <Card title='磁盘状态'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th>
                                    盘符路径
                                </th>
                                <th>
                                    文件系统
                                </th>
                                <th>
                                    盘符类型
                                </th>
                                <th>
                                    总大小
                                </th>
                                <th>
                                    可用大小
                                </th>
                                <th>
                                    已用大小
                                </th>
                                <th>
                                    已用百分比
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            {disks.map(f => {
                                return <tr key={f.name}>
                                    <td>{f.mount}</td>
                                    <td>{f.type}</td>
                                    <td>{f.name}</td>
                                    <td>{f.total}</td>
                                    <td>{f.free}</td>
                                    <td>{f.used}</td>
                                    <td><Usage value={f.usage}/></td>
                                </tr>
                            })}

                            </tbody>
                        </table>

                    </Card>

                </Col>
            </Row>


        </Page>
    }


}



