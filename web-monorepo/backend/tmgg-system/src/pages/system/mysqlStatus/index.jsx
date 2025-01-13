import React from "react";
import {Card, Table, Tabs} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        dbSize: [],
        tableSize: []
    }

    componentDidMount() {
        HttpUtil.get('mysqlStatus/dbSize').then(rs => {
            this.setState({dbSize: rs})
        })

        HttpUtil.get('mysqlStatus/tableSize').then(rs => {
            this.setState({tableSize: rs})
        })
    }


    render() {

        return <>
            <Card>
                <Tabs items={[
                    {
                        key: '1',
                        label: '数据库大小',
                        children: <Table
                            rowKey='数据库'
                            dataSource={this.state.dbSize}
                            columns={[
                                {
                                    title: '数据库',
                                    dataIndex: '数据库'
                                },
                                {
                                    title: '记录数',
                                    dataIndex: '记录数'
                                }
                                ,
                                {
                                    title: '数据容量(MB)',
                                    dataIndex: '数据容量(MB)'
                                }
                                ,
                                {
                                    title: '索引容量(MB)',
                                    dataIndex: '索引容量(MB)'
                                }
                            ]}
                            pagination={false}
                        >

                        </Table>
                    },

                    {
                        key: '2',
                        label: '表大小',
                        children: <Table
                            rowKey='id'
                            dataSource={this.state.tableSize}
                            sticky
                            columns={[
                                {
                                    title: '数据库',
                                    dataIndex: '数据库'
                                },
                                {
                                    title: '表名',
                                    dataIndex: '表名'
                                },
                                {
                                    title: '记录数',
                                    dataIndex: '记录数'
                                }
                                ,
                                {
                                    title: '数据容量(MB)',
                                    dataIndex: '数据容量(MB)'
                                }
                                ,
                                {
                                    title: '索引容量(MB)',
                                    dataIndex: '索引容量(MB)'
                                }
                            ]}
                            pagination={false}
                        >

                        </Table>
                    }
                ]}>

                </Tabs>
            </Card>


        </>

    }
}
