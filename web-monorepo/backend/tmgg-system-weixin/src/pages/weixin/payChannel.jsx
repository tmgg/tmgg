import {PlusOutlined} from '@ant-design/icons'
import {Button, Card, InputNumber, Popconfirm, Modal, Form, Input, message, Tag} from 'antd'
import React from 'react'
import {
    ButtonList,
    dictValueTag,
    FieldDateRange,
    FieldDictSelect,
    FieldRadioBoolean,
    FieldDatePickerString,
    FieldDateTimePickerString,
    HttpUtil,
    ProTable,
    FieldUploadFile, Ellipsis
} from "@tmgg/tmgg-base";
import {ViewEllipsis} from "@tmgg/tmgg-base/src/components/view/ViewEllipsis";



export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '名称',
            dataIndex: 'name',




        },

        {
            title: '应用id(appId)',
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
            title: 'p12文件(hex)',
            dataIndex: 'keyContentHex',
            render(v) {
                return <ViewEllipsis  value={v} />
            }
        },
        {
            title: '是否启用',
            dataIndex: 'enable',
            render(v){
                return v ?  <Tag color={"green"}>启用</Tag>: <Tag color={"red"}>禁用</Tag>
            }
        },
        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='weixinPayChannel:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='weixinPayChannel:delete' title='是否确定删除支付渠道'  onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]

    handleAdd = ()=>{
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record=>{
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post( 'weixinPayChannel/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }



    handleDelete = record => {
        HttpUtil.postForm( 'weixinPayChannel/delete', {id:record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='weixinPayChannel:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('weixinPayChannel/page', params)}
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
                    <Form.Item  name='id' noStyle></Form.Item>

                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='应用id(appId)' name='appId' rules={[{required: true}]} help='公众号或小程序的id'>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='商户号' name='mchId' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='商户密钥' name='mchKey' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='p12文件' name='keyContentHex' rules={[{required: true}]} help={
                        <FieldUploadFile url='weixinPayChannel/uploadP12' onChange={(value)=>this.formRef.current.setFieldsValue({
                            keyContentHex: value
                        })} />
                    }>
                        <Input.TextArea disabled rows={3}></Input.TextArea>
                    </Form.Item>
                    <Form.Item label='启用' name='enable' rules={[{required: true}]}>
                       <FieldRadioBoolean />
                    </Form.Item>
                </Form>
            </Modal>
        </>


    }
}

