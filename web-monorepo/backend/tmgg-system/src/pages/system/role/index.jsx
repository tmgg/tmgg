import {PlusOutlined} from '@ant-design/icons'
import {Button, Col, Form, Input, InputNumber, Modal, Popconfirm, Row} from 'antd'
import React from 'react'
import {
    ButtonList,
    Ellipsis,
    FieldRadioBoolean,
    FieldTableSelect,
    FieldTree,
    HttpUtil,
    Page,
    ProTable
} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,

        usersModalOpen:false
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
            render: (_, record) => {

                return (
                    <ButtonList>
                        <Button size='small' perm='sysRole:save'  onClick={() => this.handleEditUser(record)}>用户</Button>

                        <Button size='small' perm='sysRole:save' disabled={record.builtin} onClick={() => this.handleEdit(record)}>编辑</Button>
                        <Popconfirm perm='sysRole:delete' disabled={record.builtin} title='是否确定删除系统角色'
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
        this.setState({formOpen: true, formValues: record},()=>{
            HttpUtil.get('sysRole/ownMenu', {id: record.id}).then(rs=>{
                this.formRef.current.setFieldsValue({
                    menuIds: rs
                })
            })
        })
    }


    handleEditUser = record => {
        this.setState({usersModalOpen: true,formValues: record},()=>{

        })
    }
    handleAddUser =()=>{
        debugger
    }

    onFinish = values => {
        HttpUtil.post('sysRole/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.get('sysRole/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <Page >
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysRole:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params) => HttpUtil.pageData('sysRole/page', params)}
                columns={this.columns}

            />

            <Modal title='系统角色'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden
                   maskClosable={false}
                   width={600}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>


                            <Form.Item label='名称' name='name' rules={[{required: true}]}>
                                <Input/>
                            </Form.Item>

                            <Form.Item label='编码' name='code' rules={[{required: true}]}>
                                <Input/>
                            </Form.Item>



                     <Form.Item label='排序' name='seq'>
                            <InputNumber/>
                        </Form.Item>


                    <Form.Item label='备注' name='remark'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='启用' name='enabled' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>

                    <Form.Item label='菜单权限' name='menuIds' rules={[{required: true}]}>
                        <FieldTree url={'sysRole/permTree'}/>
                    </Form.Item>


                </Form>
            </Modal>


            <Modal title='角色包含的用户'
                   open={this.state.usersModalOpen}
                   footer={null}
                   destroyOnHidden
                   maskClosable={false}
                   width={800}
                   onCancel={()=>this.setState({usersModalOpen:false})}
            >


                <ProTable columns={
                    [
                        {dataIndex:'account',title:'账号'},
                        {dataIndex:'name',title:'姓名'},
                        {dataIndex:'status',title:'状态'},
                    ]
                } request={(params)=>{
                    params.id = this.state.formValues.id
                    return HttpUtil.pageData('sysRole/ownUser',params)
                }}
                          toolBarRender={() => {
                              return <ButtonList>
                                  <FieldTableSelect url={'sysUser/tableSelect'} type={'checkbox'} labelKey={'name'} />

                                  <Button perm='sysRole:save' type='primary' onClick={this.handleAddUser}>
                                       添加用户
                                  </Button>
                              </ButtonList>
                          }}
                >

                </ProTable>

            </Modal>
        </Page>


    }
}

