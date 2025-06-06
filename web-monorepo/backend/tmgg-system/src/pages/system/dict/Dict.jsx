import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ButtonList, FieldRadioBoolean,  HttpUtil, ProTable} from '@tmgg/tmgg-base'


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,
        selectedRowKeys: []
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [



        {
            title: '编码',
            dataIndex: 'code',


        },
        {
            title: '文本',
            dataIndex: 'text',
        },

        {
            title: '是否数字',
            dataIndex: 'isNumber',

            render(v) {
                return v ? '是' : '否'
            }

        },
        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => {
                if(record.builtin){
                    return
                }
                return (
                    <ButtonList>
                        <Button size='small' perm='sysDict:save' onClick={() => this.handleEdit(record)}> 编辑 </Button>
                        <Popconfirm perm='sysDict:delete' title='是否确定删除数据字典'
                                    onConfirm={() => this.handleDelete(record)}>
                            <Button size='small'>删除</Button>
                        </Popconfirm>
                    </ButtonList>
                );
            },
        },
    ]

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('sysDict/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = row => {
        HttpUtil.postForm('sysDict/delete', row).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                headerTitle='数据字典'
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysDict:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('sysDict/page', params, sort)}
                columns={this.columns}
                rowKey='id'
                search={false}

                rowSelection={{
                    type: 'radio',
                    selectedRowKeys: this.state.selectedRowKeys,
                    onChange: (selectedRowKeys, selectedRows) => {
                        this.setState({selectedRowKeys: selectedRowKeys})
                        this.props.onChange(selectedRowKeys[0])
                    }
                }}
                onRow={(record) => ({
                    onClick: () => {
                        this.setState({selectedRowKeys: [record.id]})
                        this.props.onChange(record.id)
                    }
                })}
            />

            <Modal title='数据字典'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='文本' name='text' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='编码' name='code' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='是否数字' name='isNumber' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>

                </Form>
            </Modal>


        </>


    }
}



