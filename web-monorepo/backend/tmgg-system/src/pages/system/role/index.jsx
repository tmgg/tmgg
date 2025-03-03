import {PlusOutlined} from '@ant-design/icons'
import {Button, Col, Form, Input, InputNumber, Modal, Popconfirm, Row} from 'antd'
import React from 'react'
import {ButtonList, Ellipsis, FieldRadioBoolean, FieldTree, HttpUtil, ProTable} from "@tmgg/tmgg-base";


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
            title: '编号',
            dataIndex: 'code',


        },

        {
            title: '排序',
            dataIndex: 'seq',


        },

        {
            title: '备注',
            dataIndex: 'remark',


        },

        {
            title: '启用',
            dataIndex: 'enabled',


            render(v) {
                return v == null ? null : (v ? '是' : '否')
            },


        },
        {
            title: '是否内置',
            dataIndex: 'builtin',


            render(v) {
                return v == null ? null : (v ? '是' : '否')
            },


        },

        {
            title: '权限码',
            dataIndex: 'perms',
            width: 300,
            render(v) {
                if(v){
                    return <Ellipsis>{ v.join(',')}</Ellipsis>
                }
            }

        },


        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a perm='sysRole:save' onClick={() => this.handleEdit(record)}>编辑</a>
                    <Popconfirm perm='sysRole:delete' title='是否确定删除系统角色'
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
        HttpUtil.get('sysRole/ownMenu', {id: record.id}).then(rs=>{
           this.formRef.current.setFieldsValue({
               menuIds: rs
           })
        })

        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('sysRole/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('sysRole/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysRole:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params, sort) => HttpUtil.pageData('sysRole/page', params)}
                columns={this.columns}

            />

            <Modal title='系统角色'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
                   maskClosable={false}
                   width={600}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>

                    <Row>
                        <Col span={12}>
                            <Form.Item label='名称' name='name' rules={[{required: true}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label='编号' name='code' rules={[{required: true}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row>
                        <Col span={12}> <Form.Item label='排序' name='seq'>
                            <InputNumber/>
                        </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label='启用' name='enabled' rules={[{required: true}]}>
                                <FieldRadioBoolean/>
                            </Form.Item>

                        </Col>
                    </Row>

                    <Form.Item label='备注' name='remark'>
                        <Input/>
                    </Form.Item>


                    <Form.Item label='菜单权限' name='menuIds' rules={[{required: true}]}>
                        <FieldTree url={'sysRole/permTree'}/>
                    </Form.Item>


                </Form>
            </Modal>
        </>


    }
}

