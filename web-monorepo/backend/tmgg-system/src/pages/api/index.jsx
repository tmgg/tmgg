import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'
import {
    ButtonList,
    FieldDateTimePickerString,
    FieldRadioBoolean,
    FieldRemoteMultipleSelect,
    HttpUtil,
    PageUtil,
    ProTable
} from "@tmgg/tmgg-base";
import {StrUtil} from "@tmgg/tmgg-commons-lang";


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
            title: 'appId',
            dataIndex: 'id',

        },

        {
            title: 'appSecret',
            dataIndex: 'appSecret',
        },


        {
            title: '权限',
            dataIndex: 'permsLabel',
        },

        {
            title: '准入IP',
            dataIndex: 'accessIp',
        },
        {
            title: '有效期',
            dataIndex: 'endTime',
        },
        {
            title: '启用',
            dataIndex: 'enable',
            render(v){
                return v == null ? null : ( v ? '是': '否')
            },
        },
        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' onClick={()=>PageUtil.open('/api/accountResource?accountId=' + record.id, '账户接口')}>接口管理</Button>
                    <Button size='small' onClick={()=>this.handleDoc(record)}>文档</Button>
                    <Button size='small' perm='openApiAccount:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='openApiAccount:delete' title='是否确定删除接口访客'  onConfirm={() => this.handleDelete(record)}>
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
    handleDoc = record=>{
        PageUtil.openNoLayout('/api/doc?id='+record.id)
    }


    onFinish = values => {
        HttpUtil.post( 'openApiAccount/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }



    handleDelete = record => {
        HttpUtil.postForm( 'openApiAccount/delete', {id:record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    randomAppSecret = ()=>{
        const appSecret = StrUtil.random(32)
        this.formRef.current.setFieldsValue({appSecret})
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='openApiAccount:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params) => HttpUtil.pageData('openApiAccount/page', params)}
                columns={this.columns}
            />

            <Modal title='接口访客'
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


                    <Form.Item label='准入IP' name='accessIp' >
                        <Input placeholder='逗号分隔'/>
                    </Form.Item>
                    <Form.Item label='秘钥' name='appSecret' rules={[{required: true},{len:32}]}
                               help={<Button size='small' type='link' onClick={this.randomAppSecret}>随机生成</Button>}
                    >
                        <Input />
                    </Form.Item>


                    <Form.Item label='有效期' name='endTime'  style={{marginTop:32}}>
                        <FieldDateTimePickerString />
                    </Form.Item>
                    <Form.Item label='启用' name='enable' rules={[{required: true}]}>
                        <FieldRadioBoolean />
                    </Form.Item>

                </Form>
            </Modal>
        </>


    }
}

