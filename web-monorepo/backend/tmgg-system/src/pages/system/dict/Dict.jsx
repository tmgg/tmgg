import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/pro-table'
import {http, ButtonList, HttpUtil} from "@tmgg/tmgg-base"



export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,
      selectedRowKeys:[]
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [

    {
      title: '名称',
      dataIndex: 'name',


    },

    {
      title: '编码',
      dataIndex: 'code',


    },

    {
      title: '系统内置',
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
            <Popconfirm perm='sysDict:delete' title='是否确定删除数据字典'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'sysDict/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = row => {
    HttpUtil.postForm( 'sysDict/delete', row).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          headerTitle='数据字典'
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='sysDict:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => HttpUtil.pageData('sysDict/page', params, sort)}
          columns={this.columns}
          rowKey='id'
          search={false}

          rowSelection={{
              type: 'radio',
              selectedRowKeys: this.state.selectedRowKeys,
              onChange: (selectedRowKeys,selectedRows)=>{
                  this.setState({selectedRowKeys: selectedRowKeys})
                  this.props.onChange(selectedRowKeys[0])
              }
          }}
          onRow={(record) => ({
              onClick: () => {
                  this.setState({selectedRowKeys: [record.id]})
                  this.props.onChange(record.id)
              }
          })}
      />

  <Modal title='数据字典'
    open={this.state.formOpen}
    onOk={() => this.formRef.current.submit()}
    onCancel={() => this.setState({formOpen: false})}
    destroyOnClose
    >

    <Form ref={this.formRef} labelCol={{flex: '100px'}}
        initialValues={this.state.formValues}
        onFinish={this.onFinish} >
        <Form.Item  name='id' noStyle></Form.Item>

              <Form.Item label='名称' name='name' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='编码' name='code' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>


    </Form>
  </Modal>


    </>


  }
}



