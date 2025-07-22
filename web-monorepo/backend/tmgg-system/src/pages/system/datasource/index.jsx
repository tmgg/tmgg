import {Card, Col, Descriptions, Row} from 'antd';
import React from 'react';


import {HttpUtil, Page} from "@tmgg/tmgg-base";

const {Item} = Descriptions


export default class extends React.Component {

    state = {
        status: {},
        config: {}
    }

    componentDidMount() {
        HttpUtil.get('sysDatasource/status').then(rs => {
            this.setState({status: rs})
        })
        HttpUtil.get('sysDatasource/config').then(rs => {
            this.setState({config: rs})
        })
    }

    render() {

        const {config, status} = this.state

        return <Page padding>
            <Row gutter={[16, 16]}><Col span={12}>
                <Card title='连接数'>
                    <table className='tmgg-table'>
                        <thead>
                        <tr>
                            <th width={100}>属性</th>
                            <th>值</th>
                        </tr>
                        </thead>
                        <tbody>

                        <tr>
                            <td>实时</td>
                            <td>{status.activeConnections}</td>
                        </tr>
                        <tr>
                            <td>空闲</td>
                            <td>{status.idleConnections}</td>
                        </tr>
                        <tr>
                            <td>总共</td>
                            <td>{status.totalConnections}</td>
                        </tr>

                        <tr>
                            <td>等待</td>
                            <td>{status.threadsAwaitingConnection}</td>
                        </tr>

                        </tbody>
                    </table>
                </Card>
            </Col>
                <Col span={12}>
                    <Card title='配置'>
                        <table className='tmgg-table'>
                            <thead>
                            <tr>
                                <th width={100}>属性</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>连接地址</td>
                                <td style={{wordBreak:'break-all'}}> {config.jdbcUrl}</td>
                            </tr>
                            <tr>
                                <td>驱动</td>
                                <td>{config.driverClassName}</td>
                            </tr>
                            <tr>
                                <td>最小空闲数</td>
                                <td>{config.minimumIdle}</td>
                            </tr>
                            <tr>
                                <td>空闲超时时间秒</td>
                                <td>{config.idleTimeout}</td>
                            </tr>
                            <tr>
                                <td>最大连接数</td>
                                <td>{config.maximumPoolSize}</td>
                            </tr>
                            <tr>
                                <td>连接池名称</td>
                                <td>{config.poolName}</td>
                            </tr>

                            </tbody>
                        </table>

                    </Card>
                </Col>

            </Row>


        </Page>

    }


}



