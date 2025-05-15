import {PlusOutlined} from '@ant-design/icons'
import {Button, Card, InputNumber, Popconfirm, Modal, Form, Input, message} from 'antd'
import React from 'react'
import {
    ButtonList,
    dictValueTag,
    ViewBoolean,
    FieldDateRange,
    FieldDictSelect,
    FieldRadioBoolean,
    FieldDatePickerString,
    FieldDateTimePickerString,
    Page,
    HttpUtil,
    ProTable,
    FieldRemoteSelect, PageUtil, FieldTableSelect
} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    constructor(props) {
        super(props);
        this.accountId = PageUtil.currentParams().accountId
    }

    columns = [

        {
            title: '账户',
            dataIndex: ['account', 'name'],

        },

        {
            title: '资源',
            dataIndex: ['resource', 'name'],
        },
        {
            title: '路径',
            dataIndex: ['resource', 'uri'],
        }, {
            title: '描述',
            dataIndex: ['resource', 'desc'],
        },
        {
            title: '启用',
            dataIndex: 'enable',


            render(v) {
                return <ViewBoolean value={v}/>
            },


        },

        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='openApiAccountResource:save'
                            onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='openApiAccountResource:delete' title='是否确定删除访客权限'
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
        values.account = {id: this.accountId}
        HttpUtil.post('openApiAccountResource/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('openApiAccountResource/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <Page>

            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='openApiAccountResource:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params) => HttpUtil.pageData('openApiAccountResource/page', params)}
                columns={this.columns}
            />

            <Modal title='访客权限'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden
                   maskClosable={false}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='接口' name={['resource', 'id']} rules={[{required: true}]}>
                        <FieldTableSelect url='apiResource/tableSelect' labelKey='name'/>
                    </Form.Item>



                    <Form.Item label='启用' name='enable' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>

                </Form>
            </Modal>
        </Page>


    }
}


