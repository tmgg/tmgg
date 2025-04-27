import {PlusOutlined} from '@ant-design/icons'
import {Button, Card, InputNumber, Popconfirm, Modal, Form, Input, message, Typography} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString, HttpUtil, ProTable} from "@tmgg/tmgg-base";



export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,

        importPagesOpen:false
    }

    formRef = React.createRef()

    importPagesFormRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: 'appId',
            dataIndex: 'appId',




        },
        {
            title: '标题',
            dataIndex: 'title',
        },

        {
            title: '根',
            dataIndex: 'root',
            sorter:true
        },

        {
            title: '路径',
            dataIndex: 'path',

        },
        {
            title: '全路径',
            dataIndex: 'page',
        },
        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='weixinPage:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='weixinPage:delete' title='是否确定删除微信页面'  onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]

    handleAdd = ()=>{
        this.setState({formOpen: true, formValues: {}})
    }
    handleImportPages = ()=>{
        this.setState({importPagesOpen: true})
    }

    handleEdit = record=>{
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post( 'weixinPage/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }

    handleClean=()=>{
        HttpUtil.get( 'weixinPage/clean').then(rs => {
            this.tableRef.current.reload()
        })
}

    onImportPagesFinish = values => {
        HttpUtil.post( 'weixinPage/importPages', values).then(rs => {
            this.setState({importPagesOpen: false})
            this.tableRef.current.reload()
        })
    }

    handleDelete = record => {
        HttpUtil.postForm( 'weixinPage/delete', {id:record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList maxNum={5}>
                        <Button perm='weixinPage:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>

                        <Button perm='weixinPage:save' onClick={this.handleImportPages}>导入页面</Button>

                        <Button perm='weixinPage:clean' onClick={this.handleClean}>清空</Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('weixinPage/page', params)}
                columns={this.columns}
            />

            <Modal title='微信页面'
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
                    <Form.Item  name='id' noStyle></Form.Item>

                    <Form.Item label='appId' name='appId' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='根' name='root' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='标题' name='title' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='路径' name='path' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                </Form>
            </Modal>

            <Modal title='微信页面导入'
                   open={this.state.importPagesOpen}
                   onOk={() => this.importPagesFormRef.current.submit()}
                   onCancel={() => this.setState({importPagesOpen: false})}
                   destroyOnClose
                   maskClosable={false}
                   width={800}
            >

                <Form ref={this.importPagesFormRef} labelCol={{flex: '100px'}}
                      onFinish={this.onImportPagesFinish}
                >

                    <Form.Item label='appId' name='应用表示（appId）' help='可不填' >
                        <Input/>
                    </Form.Item>

                    <Form.Item label='文件内容' name='content' rules={[{required: true}]} help='pages.json文件'>
                        <Input.TextArea rows={10}/>
                    </Form.Item>

                </Form>
            </Modal>



        </>


    }
}

