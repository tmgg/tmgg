import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ButtonList, HttpUtil, PageUtil, ProTable} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [
        {
            title: '编码',
            dataIndex: 'code',
        },

        {
            title: '标题',
            dataIndex: 'title',
        },


        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a perm='sysChart:save'
                       onClick={() => PageUtil.open('/chart/sysChart/design?id=' + record.id)}> 设计 </a>

                    <Popconfirm perm='sysChart:delete' title='是否确定删除系统图表'
                                onConfirm={() => this.handleDelete(record)}>
                        <a>删除</a>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('sysChart/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('sysChart/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysChart:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('sysChart/page', params)}
                columns={this.columns}
                searchFormItemsRender={() => {
                    return <>

                        <Form.Item label='编码' name='code'>
                            <Input/>
                        </Form.Item>
                        <Form.Item label='标题' name='title'>
                            <Input/>
                        </Form.Item>


                    </>
                }}
            />

            <Modal title='系统图表'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
                   maskClosable={false}
                   width={800}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>
                    <Form.Item label='编码' name='code' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='标题' name='title' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                </Form>
            </Modal>
        </>


    }
}

