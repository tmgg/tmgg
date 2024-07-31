import {PlusOutlined} from '@ant-design/icons';
import {Button, Card, Empty, message, Modal, Popconfirm, Tree} from 'antd';
import React from 'react';
import {ProTable} from "@ant-design/pro-components";
import UserOrgForm from "./UserOrgForm";
import {ButtonList, dictValue, dictValueTag, hasPermission, HttpClient, LeftRightLayout} from "../../../common";

const baseTitle = "用户"
const baseApi = 'sysUser/';
const basePerm = 'sysUser:';

const addTitle = "添加" + baseTitle
const editTitle = '编辑' + baseTitle
const deleteTitle = '删除' + baseTitle


const addApi = baseApi + 'add'
const editApi = baseApi + 'edit'
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
  actionRef = React.createRef();
  orgFormRef = React.createRef();

  columns = [
    {
      title: '单位',
      dataIndex: 'orgLabel',
      hideInSearch: true,
      hideInForm: true
    },
    {
      title: '单位',
      dataIndex: 'orgId',
      hideInTable: true,
      hideInSearch: true,
      valueType: 'remoteTreeSelect',
      params: '/sysOrg/tree?type=UNIT',
      formItemProps: {
        required: true,
        rules: [{required: true}]
      }
    },
    {
      title: '部门',
      dataIndex: 'deptLabel',
      hideInSearch: true,
      hideInForm: true
    },
    {
      title: '部门',
      dataIndex: 'deptId',
      hideInTable: true,
      hideInSearch: true,
      valueType: 'remoteTreeSelect',
      params: '/sysOrg/tree?type=DEPT'
    },
    {
      title: '姓名',
      dataIndex: 'name',
      formItemProps: {
        required: true,
        rules: [{required: true}]
      }
    },
    {
      title: '登录账号',
      dataIndex: 'account',
      formItemProps: {
        required: true,
        rules: [{required: true}]
      }
    },


    {
      title: '手机',
      dataIndex: 'phone'
    },
    {
      title: '角色',
      dataIndex: 'roleIds',
      render: (roleNames, row) => {
        if (row.roleNames) {
          return row.roleNames.join(',')
        }
      },

      valueType: 'remoteMultipleSelect',
      params: '/sysRole/options',
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInSearch: true,
      valueType: 'dictRadio',
      params: 'common_status',

      formItemProps: {
        required: true,
        rules: [{required: true}]
      }

    },
    {
      title: '数据权限',
      dataIndex: 'dataPermType',
      hideInSearch: true,
      hideInForm: true,
      render(_,item){

        return dictValueTag('data_perm_type', item.dataPermType)
      }
    },
    {
      title: '唯一标识',
      dataIndex: 'id',
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      hideInSearch: true,
      hideInForm: true,

    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return <ButtonList>
          <a perm={editPerm} onClick={() => this.edit(record)}> 修改 </a>

          <a perm='sysUser:grantData' onClick={() => this.orgFormRef.current.show(record)}> 数据权限 </a>


          <Popconfirm perm='sysUser:resetPwd' title='确认重置密码？' onConfirm={() => this.resetPwd(record)}>
            <a>重置密码</a>
          </Popconfirm>

          <Popconfirm perm={delPerm} title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>
        </ButtonList>;
      },
    },
  ];

  edit(record) {
    this.setState({
      showEditForm: true,
      formValues: null
    })

    HttpClient.get(baseApi + 'detail', {id: record.id}).then(rs => {
      this.setState({formValues: rs.data})
    })
  }

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

  handleSave = value => {
    HttpClient.post(addApi, value).then(rs => {
      this.setState({showAddForm: false})
      this.actionRef.current.reload();
      message.success(rs.message)
    })
  }

  handleUpdate = value => {
    let params = {id: this.state.formValues.id, ...value}
    HttpClient.post(editApi, params).then(rs => {
      this.setState({showEditForm: false})
      this.actionRef.current.reload();
    })
  }

  handleDelete = r => {
    HttpClient.get(delApi, {id: r.id}).then(rs => {
      this.actionRef.current.reload();
    })
  }

  handleExport = ()=>{
    HttpClient.downloadFile("sysUser/export").then(rs => {
      message.success('下载成功')
    })
  }

  onSelectOrg = orgIds => {
    if (orgIds.length > 0) {
      this.setState({currentOrgId: orgIds[0]})
    } else {
      this.setState({currentOrgId: null})
    }

    this.actionRef.current.reload()
  }

  render() {
    let {showAddForm, showEditForm, treeData} = this.state

    return <>
      <LeftRightLayout leftSize={250}>
        <Card title={'组织机构'}>

          {treeData.length > 0 ? <Tree
            treeData={treeData}
            defaultExpandAll
            onSelect={this.onSelectOrg}
          >
          </Tree> : <Empty description={'组织机构数据为空'}>

          </Empty>}
        </Card>

        <Card title='用户列表'>
          <ProTable
            actionRef={this.actionRef}
            toolBarRender={(action, {selectedRows}) => {
              const menus = []

              if (hasPermission(addPerm)) {
                menus.push(<Button type="primary"
                                   onClick={() => {
                  this.setState({showAddForm: true})
                }}>
                  <PlusOutlined/> 新增
                </Button>)

                menus.push(<Button
                  onClick={this.handleExport}>导出</Button>)
              }
              return menus
            }}
            request={(params, sort) => {
              params.orgId = this.state.currentOrgId
              return HttpClient.getPageableData(pageApi, params, sort)
            }
            }
            columns={this.columns}
            rowSelection={false}
            rowKey="id"
          /> </Card>
      </LeftRightLayout>


      <Modal
        maskClosable={false}
        destroyOnClose
        title={addTitle}
        open={showAddForm}
        onCancel={() => {
          this.state.showAddForm = false;
          this.setState({showAddForm: false})
        }}
        footer={null}
        width={800}
      >
        <ProTable
          onSubmit={this.handleSave}
          form={{layout: 'horizontal', labelCol: {span: 3}}}
          type="form"
          columns={this.columns}
          rowSelection={{}}
        />
      </Modal>

      <Modal
        maskClosable={false}
        destroyOnClose
        title={editTitle}
        visible={showEditForm}
        onCancel={() => {
          this.setState({showEditForm: false})
        }}
        footer={null}
        width={800}
      >
        {this.state.formValues && <ProTable
          onSubmit={this.handleUpdate}
          form={{initialValues: this.state.formValues, layout: 'horizontal', labelCol: {span: 3}}}
          type="form"
          columns={this.columns}
        />}
      </Modal>

      <UserOrgForm ref={this.orgFormRef}/>

    </>
  }


}



