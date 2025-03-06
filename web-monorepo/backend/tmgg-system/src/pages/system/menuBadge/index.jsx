import {PlusOutlined} from '@ant-design/icons'
import {Button, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList, FieldRemoteTreeSelect, HttpUtil, ProTable} from "@tmgg/tmgg-base";



export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [

    {
      title: '菜单ID',
      dataIndex: 'menuId',
    },
      {
          title: '菜单',
          dataIndex: 'menuName',
      },
    {
      title: '请求地址',
      dataIndex: 'url',




    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
          <ButtonList>
            <Button size='small' perm='sysMenuBadge:save' onClick={() => this.handleEdit(record)}>编辑</Button>
            <Popconfirm perm='sysMenuBadge:delete' title='是否确定删除菜单小红点'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'sysMenuBadge/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.postForm( 'sysMenuBadge/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='sysMenuBadge:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => HttpUtil.pageData('sysMenuBadge/page', params)}
          columns={this.columns}

      />

  <Modal title='菜单小红点'
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

              <Form.Item label='菜单ID' name='menuId' rules={[{required: true}]}>
                  <FieldRemoteTreeSelect url='sysMenuBadge/menuOptions' rules={[{required:true}]} />

              </Form.Item>
              <Form.Item label='请求地址' name='url' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>

    </Form>
  </Modal>
    </>


  }
}


