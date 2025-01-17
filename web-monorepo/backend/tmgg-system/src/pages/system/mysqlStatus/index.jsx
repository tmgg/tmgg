import React from "react";
import {Card, Splitter, Table, Tabs} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        dbSize: [],
        tableSize: [],
        schema: null
    }

    componentDidMount() {
        HttpUtil.get('mysqlStatus/dbSize').then(rs => {
            this.setState({dbSize: rs})
        })
        this.loadTableSize();
    }

    loadTableSize = () => {
        this.setState({tableSize:[]})
        HttpUtil.get('mysqlStatus/tableSize', {schema:this.state.schema}).then(rs => {
            this.setState({tableSize: rs})
        })
    };

    onSelect = (record)=>{
        this.setState({schema: record.schema},this.loadTableSize)
    }


    render() {

        return <>
            <Card>
                <Splitter>
                    <Splitter.Panel defaultSize={500}>
                        <Card title='数据库空间'>
                            <Table
                                rowKey='schema'
                                dataSource={this.state.dbSize}
                                rowSelection={{
                                    type:'radio',
                                    onSelect: this.onSelect
                                }}
                                columns={[
                                    {
                                        title: '数据库',
                                        dataIndex: 'schema'
                                    },
                                    {
                                        title: '记录数',
                                        dataIndex: '记录数'
                                    }
                                    ,
                                    {
                                        title: '数据(MB)',
                                        dataIndex: '数据容量(MB)'
                                    }
                                    ,
                                    {
                                        title: '索引(MB)',
                                        dataIndex: '索引容量(MB)'
                                    }
                                ]}
                                pagination={false}
                            >

                            </Table>
                        </Card>
                    </Splitter.Panel>
                    <Splitter.Panel>
                        <Card title='表空间'>
                            <Table
                                rowKey='id'
                                dataSource={this.state.tableSize}
                                size="small"
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
                                        title: '数据(MB)',
                                        dataIndex: '数据容量(MB)'
                                    }
                                    ,
                                    {
                                        title: '索引(MB)',
                                        dataIndex: '索引容量(MB)'
                                    }
                                ]}
                                pagination={false}
                            >

                            </Table>
                        </Card>
                    </Splitter.Panel>
                </Splitter>

            </Card>


        </>

    }
}
