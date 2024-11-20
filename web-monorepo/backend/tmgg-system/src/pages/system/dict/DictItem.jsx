import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, InputNumber, Modal, Popconfirm, Tag} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/tmgg-base'
import {ButtonList, FieldDictSelect, FieldRadioBoolean, http, HttpUtil} from "@tmgg/tmgg-base"


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.sysDictId !== this.props.sysDictId) {
            this.tableRef.current.reload()
        }
    }

    columns = [

        {
            title: '键',
            dataIndex: 'key',
        },
        {
            title: '文本',
            dataIndex: 'text',
        },
        {
            title: '状态',
            dataIndex: 'enabled',
            valueType: 'boolean',
        },
        {
            title: '显示颜色',
            dataIndex: 'color',
            render(v) {
                return <Tag color={v}>COLOR</Tag>
            }
        },
        {
            title: '系统内置',
            dataIndex: 'builtin',
            valueType: 'boolean',
        },
        {
            title: '序号',
            dataIndex: 'seq',
            valueType: 'digit',
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a perm='sysDictItem:save' onClick={() => this.handleEdit(record)}> 修改 </a>
                    <Popconfirm perm='sysDictItem:delete' title='是否确定删除字典项'
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
        values.sysDict = {id: this.props.sysDictId}
        HttpUtil.post('sysDictItem/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = row => {
        HttpUtil.postForm('sysDictItem/delete', row).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                headerTitle='字典项列表'
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysDictItem:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => {
                    params.sysDictId = this.props.sysDictId
                    return HttpUtil.pageData('sysDictItem/page', params, sort);
                }}
                columns={this.columns}
                rowKey='id'
                search={false}
            />

            <Modal
                title='编辑数据字典项'
                open={this.state.formOpen}
                onOk={() => this.formRef.current.submit()}
                onCancel={() => this.setState({formOpen: false})}
                destroyOnClose
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>


                    <Form.Item label='键' name='key' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='文本' name='text' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='状态' name='enabled' rules={[{required: true}]}>
                        <FieldRadioBoolean />
                    </Form.Item>
                    <Form.Item label='颜色' name='color' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='系统内置' name='builtin' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>
                    <Form.Item label='序号' name='seq' rules={[{required: true}]}>
                        <InputNumber/>
                    </Form.Item>

                </Form>
            </Modal>
        </>


    }
}



