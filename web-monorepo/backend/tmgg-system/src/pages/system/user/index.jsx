import {PlusOutlined} from '@ant-design/icons';
import {
    Button,
    Card,
    Form,
    Input,
    message,
    Modal,
    Popconfirm,
    Splitter
} from 'antd';
import React from 'react';
import {FieldRadioBoolean,ProTable, HttpUtil} from "@tmgg/tmgg-base";
import UserPerm from "./UserPerm";
import {
    ButtonList,
    FieldOrgTreeSelect
} from "@tmgg/tmgg-base";
import {OrgTree} from "@tmgg/tmgg-base";

const baseTitle = "用户"
const baseApi = 'sysUser/';
const basePerm = 'sysUser:';

const deleteTitle = '删除' + baseTitle


const delApi = baseApi + 'delete'
const pageApi = baseApi + 'page'

const addPerm = basePerm + 'add'
const delPerm = basePerm + 'delete'
const editPerm = basePerm + 'edit'

export default class extends React.Component {

    state = {
        showAddForm: false,
        showEditForm: false,
        formValues: {},
        treeData: [],

        currentOrgId: null
    }
    permRef = React.createRef();

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [
        {
            title: '单位',
            dataIndex: 'unitLabel',
        },
        {
            title: '部门',
            dataIndex: 'deptLabel',
        },
        {
            title: '姓名',
            dataIndex: 'name',
            sorter:true
        },
        {
            title: '登录账号',
            dataIndex: 'account',
            sorter:true
        },


        {
            title: '手机',
            dataIndex: 'phone'
        },
        {
            title: '邮箱',
            dataIndex: 'email'
        },
        {
            title: '角色',
            dataIndex: 'roleIds',
            render: (_, row) => {
                if (row.roleNames) {
                    return row.roleNames.join(',')
                }
            },
        },
        {
            title: '状态',
            dataIndex: 'enabled',
            valueType: 'boolean',
        },
        {
            title: '数据权限',
            dataIndex: 'dataPermType',
            valueType: 'dictRadio',
            params: 'dataPermType',
        },
        {
            title: 'id',
            dataIndex: 'id',
        },
        {
            title: '修改时间',
            dataIndex: 'updateTime',
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => {
                return <ButtonList>
                    <Button size='small' perm={editPerm} onClick={() => this.handleEdit(record)}> 修改 </Button>

                    <Button size='small' perm='sysUser:grantData' onClick={() => this.permRef.current.show(record)}> 授权 </Button>

                    <Popconfirm perm='sysUser:resetPwd' title='确认重置密码？' onConfirm={() => this.resetPwd(record)}>
                        <a>重置密码</a>
                    </Popconfirm>

                    <Popconfirm perm={delPerm} title={'是否确定' + deleteTitle}
                                onConfirm={() => this.handleDelete(record)}>
                        <a>删除</a>
                    </Popconfirm>
                </ButtonList>;
            },
        },
    ];



    componentDidMount() {
        HttpUtil.get('sysOrg/tree').then(rs => {
            this.setState({treeData: rs})
        })
    }

    resetPwd(row) {
        HttpUtil.post('/sysUser/resetPwd', {id: row.id}).then(rs => {
            Modal.success({
                title: '成功',
                content: rs.message
            })
        })
    }


    handleDelete = r => {
        HttpUtil.get(delApi, {id: r.id}).then(rs => {
            this.tableRef.current.reload();
        })
    }

    handleExport = () => {
        HttpUtil.downloadFile("sysUser/export").then(rs => {
            message.success('下载成功')
        })
    }

    onSelectOrg = key => {
        this.setState({currentOrgId: key})
        this.tableRef.current.reload()
    }

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record => {
        record.deptId = record.deptId || record.unitId
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('sysUser/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }

    render() {

        return <>
            <Splitter>
                <Splitter.Panel defaultSize={300}>
                <Card title='机构树'>
                    <OrgTree onChange={this.onSelectOrg}/>
                </Card>
                </Splitter.Panel>
                <Splitter.Panel>
                    <ProTable
                        searchForm={false}
                        searchInput={true}
                        actionRef={this.tableRef}
                        toolBarRender={(action, {selectedRows}) => {

                            return <ButtonList>
                                <Button
                                    perm={addPerm}
                                    type="primary"
                                        onClick={this.handleAdd}>
                                    <PlusOutlined/> 新增
                                </Button>

                                <Button  perm={addPerm}
                                    onClick={this.handleExport}>导出</Button>
                            </ButtonList>
                        }}
                        request={(params, sort) => {
                            params.orgId = this.state.currentOrgId
                            return HttpUtil.pageData(pageApi, params, sort)
                        }
                        }
                        columns={this.columns}
                        rowKey="id"
                        scroll={{x:'max-content'}}
                    /> </Splitter.Panel>
            </Splitter>


            <Modal title='系统用户'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='所属机构' name='deptId' rules={[{required: true}]}>
                        <FieldOrgTreeSelect/>
                    </Form.Item>

                    <Form.Item label='姓名' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='账号' name='account' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>


                    <Form.Item label='电话' name='phone'>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='邮箱' name='email'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='状态' name='enabled' rules={[{required: true}]}>
                        <FieldRadioBoolean />
                    </Form.Item>

                </Form>
            </Modal>


            <UserPerm ref={this.permRef} onOk={()=>this.tableRef.current.reload()}/>

        </>
    }


}



