import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, InputNumber, Modal, Popconfirm} from 'antd'
import React from 'react'
import {
    ButtonList,
    FieldFileBase64,
    FieldRadioBoolean,
    FieldRemoteSelect,
    FieldUploadImage,
    HttpUtil,
    ProTable, ViewBoolean,
    ViewEllipsis,
    ViewImage
} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '支付方式',
            dataIndex: 'methodName',
        },  {
            title: '显示名',
            dataIndex: 'methodDisplayName',
        },
        {
            title: '支付编码',
            dataIndex: 'methodCode',
        },

        {
            title: '是否线下',
            dataIndex: 'methodOffline',
            render(v) {
                return <ViewBoolean value={v} />;
            }
        },
        {
            title: '图标',
            dataIndex: 'icon',
            render(v) {
                return <ViewImage value={v}/>
            }


        },

        {
            title: '备注',
            dataIndex: 'remark',


        },

        {
            title: '排序',
            dataIndex: 'seq',


            render(v) {
                return v == null ? null : (v ? '是' : '否')
            },


        },

        {
            title: '启用',
            dataIndex: 'enable',


            render(v) {
                return v == null ? null : (v ? '是' : '否')
            },


        },

        {
            title: '应用ID',
            dataIndex: 'appId',


        },

        {
            title: '商户号',
            dataIndex: 'mchId',


        },

        {
            title: '商户密钥',
            dataIndex: 'mchKey',


        },

        {
            title: 'p12文件',
            dataIndex: 'p12FileBase64', render(v) {
                return <ViewEllipsis value={v}/>;
            }
        },

        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='paymentChannel:save'
                            onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='paymentChannel:delete' title='是否确定删除支付渠道'
                                onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
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
        HttpUtil.post('paymentChannel/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('paymentChannel/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='paymentChannel:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('paymentChannel/page', params)}
                columns={this.columns}
            />

            <Modal title='支付渠道'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
                   maskClosable={false}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='支付方式' name='methodCode' rules={[{required: true}]}>
                        <FieldRemoteSelect url='paymentChannel/paymentMethodOptions'/>
                    </Form.Item>

                    <Form.Item label='应用ID' name='appId' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='商户号' name='mchId' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='商户密钥' name='mchKey' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='p12文件' name='p12FileBase64' rules={[{required: true}]}>
                        <FieldFileBase64/>
                    </Form.Item>


                    <Form.Item label='图标' name='icon'>
                        <FieldUploadImage/>
                    </Form.Item>
                    <Form.Item label='排序' name='seq' rules={[{required: true}]}>
                        <InputNumber/>
                    </Form.Item>
                    <Form.Item label='备注' name='remark' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='启用' name='enable' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>
                </Form>
            </Modal>
        </>


    }
}


