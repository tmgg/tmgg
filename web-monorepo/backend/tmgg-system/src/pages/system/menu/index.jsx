import React from "react";
import {Button, Form, Modal, Popconfirm, Radio, Space, Table} from "antd";
import {dictValueTag, FieldRadioBoolean, HttpUtil, NamedIcon} from "@tmgg/tmgg-base";
import * as Icons from '@ant-design/icons'

let iconNames = Object.keys(Icons);
iconNames = iconNames.filter(n => n.endsWith("Outlined"))

export default class extends React.Component {

    state = {
        treeData: [],

        processing: false,

        iconModalOpen: false,

        formValues: {},
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

    handleChangeIcon = () => {
        HttpUtil.post('sysMenu/changeIcon', this.state.formValues).then(rs => {
            this.loadData()
        }).finally(() => {
            this.setState({iconModalOpen: false})
        })
    };
    handleDelete = record => {
        HttpUtil.postForm('sysMenu/delete', {id: record.id}).then(rs => {
            this.loadData()
        })
    }

    render() {
        return <>
            <div>
                <Form>
                    <Form.Item label='显示按钮'>
                        <FieldRadioBoolean/>
                    </Form.Item>
                </Form>
            </div>

            {this.state.treeData.length > 0 && <Table
                rowKey='id'
                expandable={{
                    defaultExpandAllRows: true
                }}
                size={"small"}
                dataSource={this.state.treeData}
                columns={[
                    {
                        title: '名称',
                        dataIndex: 'name',
                        render(v, record) {
                            return <div><NamedIcon name={record.icon}/> {v} </div>
                        }
                    },
                    {
                        title: '权限',
                        dataIndex: 'perm'
                    },
                    {
                        title: '类型',
                        dataIndex: 'type',
                        width: 80,
                        render(v) {
                            return dictValueTag('menuType', v)
                        }
                    },

                    {
                        title: '路由',
                        dataIndex: 'path'
                    },
                    {
                        title: '菜单可见',
                        dataIndex: 'visible', width: 80,
                        render(v) {
                            if (v) {
                                return v ? '是' : '否'
                            }
                        }
                    },
                    {
                        title: '操作',
                        dataIndex: 'option',
                        width: 100,
                        render: (v, record) => {
                            return <Space>

                                <Button disabled={!record.visible}
                                        size='small'
                                        onClick={() => {
                                            this.setState({iconModalOpen: true, formValues: record})
                                        }}>编辑图标</Button>

                                <Popconfirm perm='sysAsset:delete' title='是否确定删除菜单'
                                            onConfirm={() => this.handleDelete(record)}>
                                    <Button size='small'>删除</Button>
                                </Popconfirm>
                            </Space>
                        }
                    },
                ]}
                pagination={false}
            >
            </Table>}

            <Modal title='编辑图标'
                   width={800}
                   open={this.state.iconModalOpen}

                   onCancel={() => {
                       this.setState({iconModalOpen: false})
                   }}

                   onOk={this.handleChangeIcon}
            >
                当前图标： {this.state.formValues.icon}
                <div style={{height: 600, overflowY: 'auto'}}>
                    <Radio.Group buttonStyle="solid"
                                 value={this.state.formValues.icon}
                                 onChange={e => {
                                     const {formValues} = this.state
                                     formValues.icon = e.target.value
                                     this.setState({formValues})
                                 }}
                    >
                        {iconNames.map(iconName => {
                            return <Radio.Button value={iconName}>
                                <NamedIcon name={iconName} style={{fontSize: 20}} title={iconName}/></Radio.Button>
                        })}
                    </Radio.Group>
                </div>

            </Modal>


        </>


    }
}
