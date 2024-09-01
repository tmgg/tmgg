import {PlusOutlined} from '@ant-design/icons';
import {
    Button,
    Card,
    Form,
    Input,
    Layout,
    message,
    Modal,
    Popconfirm,
    Typography
} from 'antd';
import React from 'react';
import {ProTable} from "@tmgg/pro-table";
import {http} from "@tmgg/tmgg-base";
import UserPerm from "./UserPerm";
import {
    ButtonList,
    dictValueTag, FieldDictRadio,
    FieldOrgTreeSelect, hasPermission,
    HttpClient,
    LeftRightLayout
} from "../../../common";
import OrgTree from "../../../commponents/OrgTree";

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
        },
        {
            title: '登录账号',
            dataIndex: 'account',
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
            dataIndex: 'status',
            valueType: 'dictRadio',
            params: 'common_status',
        },
        {
            title: '数据权限',
            dataIndex: 'dataPermType',
            valueType: 'dictRadio',
            params: 'data_perm_type',
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
        HttpClient.get('sysOrg/tree').then(rs => {
            this.setState({treeData: rs.data})
        })
    }

    resetPwd(row) {
        HttpClient.post('/sysUser/resetPwd', {id: row.id}).then(rs => {
            Modal.success({
                title: '成功',
                content: rs.message
            })
        })
    }


    handleDelete = r => {
        HttpClient.get(delApi, {id: r.id}).then(rs => {
            this.tableRef.current.reload();
        })
    }

    handleExport = () => {
        HttpClient.downloadFile("sysUser/export").then(rs => {
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
        HttpClient.post('sysUser/save', values).then(rs => {
            message.success(rs.message)
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }

    render() {

        return <>
            <LeftRightLayout>
                <Card>
                    <div style={{marginBottom: '1rem'}}
                    >
                        <Typography.Text>组织机构</Typography.Text>
                    </div>
                    <OrgTree onChange={this.onSelectOrg}/>
                </Card>

                <>
                    <ProTable
                        size={'small'}
                        search={false}
                        options={{search: true}}
                        actionRef={this.tableRef}
                        toolBarRender={(action, {selectedRows}) => {
                            const menus = []

                            if (hasPermission(addPerm)) {
                                menus.push(<Button type="primary"
                                                   onClick={this.handleAdd}>
                                    <PlusOutlined/> 新增
                                </Button>)

                                menus.push(<Button
                                    onClick={this.handleExport}>导出</Button>)
                            }
                            return menus
                        }}
                        request={(params, sort) => {
                            params.orgId = this.state.currentOrgId
                            return http.requestPageData(pageApi, params, sort)
                        }
                        }
                        columns={this.columns}
                        rowKey="id"
                        scroll={{x:'max-content'}}
                    /> </>
            </LeftRightLayout>


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

                    <Form.Item label='状态' name='status' rules={[{required: true}]}>
                        <FieldDictRadio typeCode='common_status'/>
                    </Form.Item>


                </Form>
            </Modal>


            <UserPerm ref={this.permRef} onOk={()=>this.tableRef.current.reload()}/>

        </>
    }


}



