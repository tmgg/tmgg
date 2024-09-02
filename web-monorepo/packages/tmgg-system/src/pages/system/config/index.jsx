import {PlusOutlined} from '@ant-design/icons'
import {Button, Popconfirm,Modal,Form,Input} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/pro-table'
import {ButtonList, FieldText, HttpUtil} from "@tmgg/tmgg-base";



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
      title: '键',
      dataIndex: 'key',


    },

    {
      title: '值',
      dataIndex: 'value',


    },

    {
      title: '默认值',
      dataIndex: 'defaultValue',


    },

    {
      title: '备注',
      dataIndex: 'remark',


    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
          <ButtonList>
            <a perm='sysConfig:save' onClick={() => this.handleEdit(record)}> 修改 </a>
          </ButtonList>
      ),
    },
  ]


  handleEdit = record=>{
    this.setState({formOpen: true, formValues: record})
  }


  onFinish = values => {
    HttpUtil.post( 'sysConfig/save', values ).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }





  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='sysConfig:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => HttpUtil.pageData('sysConfig/page', params, sort)}
          columns={this.columns}
          rowKey='id'
      />

      <Modal title='系统配置'
             open={this.state.formOpen}
             onOk={() => this.formRef.current.submit()}
             onCancel={() => this.setState({formOpen: false})}
             destroyOnClose
      >

        <Form ref={this.formRef} labelCol={{flex: '100px'}}
              initialValues={this.state.formValues}
              onFinish={this.onFinish} >
          <Form.Item  name='id' noStyle></Form.Item>

          <Form.Item label='名称' name='name' >
            <FieldText/>
          </Form.Item>
          <Form.Item label='键' name='key' >
            <FieldText/>
          </Form.Item>
          <Form.Item label='值' name='value' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='默认值' name='defaultValue' >
            <FieldText/>
          </Form.Item>
          <Form.Item label='备注' name='remark' >
            <FieldText/>
          </Form.Item>

        </Form>
      </Modal>
    </>


  }
}



