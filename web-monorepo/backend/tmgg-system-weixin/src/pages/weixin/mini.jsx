import {Button, Form, Input, Modal, Popconfirm, Space} from 'antd'
import React from 'react'
import {PlusOutlined} from "@ant-design/icons";

import {ButtonList, HttpUtil} from "@tmgg/tmgg-base";
import { ProTable} from "@tmgg/tmgg-base";
export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,

    selectedRowKeys: [],

    jobClassOptions: []
  }



  tableRef = React.createRef()
  formRef = React.createRef()

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: 'appId',
      dataIndex: 'appId',
    },
    {
      title: '密钥',
      dataIndex: 'appSecret',
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => {
        return <ButtonList>
          <Button size='small' onClick={() => this.handleEdit(record)}> 编辑 </Button>
          <Popconfirm title='是否确定删除?' onConfirm={() => this.handleDelete(record)}>
            <Button size='small'>删除</Button>
          </Popconfirm>
        </ButtonList>;
      },
    },

  ]

  handleAdd = () => {
    this.setState({formOpen: true,formValues:{}})
  }
  handleEdit = (record) => {
    this.setState({formOpen: true, formValues: record})
  }


  onFinish = (values) => {
    HttpUtil.post('weixinMini/save', values).then(rs => {
        this.setState({formOpen: false})
        this.tableRef.current.reload();
    })
  }

  handleDelete = row => {
    HttpUtil.postForm('weixinMini/delete', null,{id: row.id}).then(rs => {
      this.tableRef.current.reload();
    })
  }
  render() {
    return <div >
      <ProTable
        actionRef={this.tableRef}
        toolBarRender={(action, {selectedRowKeys}) => {
          return <Button type='primary' onClick={() => this.handleAdd()} icon={<PlusOutlined/>}>
            新建
          </Button>
        }}
        request={(params, sort) => {
          return HttpUtil.pageData('weixinMini/page', params, sort)
        }}
        columns={this.columns}
        rowSelection={false}
        rowKey='id'
        bordered
        search={false}
      />


      <Modal title='微信小程序'
             open={this.state.formOpen}
             destroyOnClose
             onOk={() => this.formRef.current.submit()}
             onCancel={() => this.setState({formOpen: false})}
      >

        <Form ref={this.formRef}
              layout='vertical'
              initialValues={this.state.formValues}
              onFinish={this.onFinish}>

          <Form.Item name='id' noStyle>
          </Form.Item>
          <Form.Item label='名称' name='name' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='appId' name='appId' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='密钥' name='appSecret' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='备注' name='remark' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
        </Form>

      </Modal>

    </div>
  }
}



