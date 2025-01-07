import React from "react";
import {Button, Card, Table} from "antd";
import {dictValueTag, HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        treeData: [],
        processing: false
    }

    componentDidMount() {
        this.loadData()
    }

    loadData = () => {
        this.setState({treeData: []})
        HttpUtil.get('/sysMenu/list').then(rs => {
            this.setState({treeData: rs})
        })

    };

    onClickReset = () => {
        this.setState({processing: true})
        HttpUtil.get('sysMenu/reset').then(rs => {
            this.loadData()
        }).finally(() => {
            this.setState({processing: false})
        })
    };

    render() {


        return <>


            <Card title='菜单列表' extra={<Button type='primary' onClick={this.onClickReset}
                                                  loading={this.state.processing}>重置</Button>}>
                {this.state.treeData.length > 0 && <Table
                    expandable={{
                        defaultExpandAllRows: true
                    }}
                    size={"small"}
                    dataSource={this.state.treeData}
                    columns={[
                        {title: '名称', dataIndex: 'name'},
                        {title: '权限', dataIndex: 'perm'},
                        {
                            title: '类型', dataIndex: 'type', render(v) {
                                return dictValueTag('menuType', v)
                            }
                        },
                        {title: '图标', dataIndex: 'icon'},
                        {title: '路由', dataIndex: 'path'},
                        {
                            title: '菜单可见', dataIndex: 'visible', render(v) {
                                return v + ""
                            }
                        },

                        {title: '主键', dataIndex: 'id'},
                        {title: '父键', dataIndex: 'pid'},
                    ]}
                    pagination={false}
                >
                </Table>}
            </Card>


        </>


    }
}
