import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/pro-table'
import {http} from "@tmgg/tmgg-base"

import {ButtonList,FieldRadioBoolean} from "@tmgg/tmgg-system";



export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [
    
    {
      title: 'name',
      dataIndex: 'name',


    },

    {
      title: 'code',
      dataIndex: 'code',


    },

    {
      title: 'builtin',
      dataIndex: 'builtin',

       valueType: 'boolean',

    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
          <ButtonList>
            <a perm='sysDict:save' onClick={() => this.handleEdit(record)}> 修改 </a>
            <Popconfirm perm='sysDict:delete' title='是否确定删除SysDict'  onConfirm={() => this.handleDelete(record)}>
              <a>删除</a>
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
    http.post( 'sysDict/save', values).then(rs => {
      message.success(rs.message)
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = row => {
    http.post( 'sysDict/delete', row).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='sysDict:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => http.requestPageData('sysDict/page', params, sort)}
          columns={this.columns}
          rowSelection={false}
          rowKey='id'
          columnEmptyText={false}
          bordered
      />

  <Modal title='SysDict'
    open={this.state.formOpen}
    onOk={() => this.formRef.current.submit()}
    onCancel={() => this.setState({formOpen: false})}
    destroyOnClose
    >

    <Form ref={this.formRef} labelCol={{flex: '100px'}}
        initialValues={this.state.formValues}
        onFinish={this.onFinish} >
        <Form.Item  name='id' noStyle></Form.Item>

              <Form.Item label='name' name='name' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='code' name='code' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='builtin' name='builtin' rules={[{required: true}]}>
                   <FieldRadioBoolean />
              </Form.Item>

    </Form>
  </Modal>
</>


  }
}



