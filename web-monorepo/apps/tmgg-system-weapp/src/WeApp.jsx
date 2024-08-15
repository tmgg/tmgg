import {Button, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Tag} from 'antd'
import React from 'react'
import {PlusOutlined} from "@ant-design/icons";
import {ProTable} from "@ant-design/pro-table";
import {http} from "@tmgg/tmgg-base";
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
      title: '应用id',
      dataIndex: 'appId',
    },
    {
      title: '密钥',
      dataIndex: 'appSecret',
      hideInSearch:true,
      valueType: "password"
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return (
          <Space>

            <Button size='small' onClick={() => this.handleEdit(record)}> 修改 </Button>
            <Popconfirm title='是否确定删除?' onConfirm={() => this.handleDelete(record)}>
              <Button size='small'>删除</Button>
            </Popconfirm>
          </Space>
        );
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
    http.post('weapp/save', values).then(rs => {
      if(rs.success){
        this.setState({formOpen: false})
        this.tableRef.current.reload();
        message.success(rs.message)
      }else {
        Modal.error({title:'操作异常',content:rs.message})
      }

    }).catch(err => {
      Modal.error({title:'操作异常',content:err})
    })
  }

  handleDelete = row => {
    const  hide =message.loading("删除任务中...")
    http.get('weapp/delete', {id: row.id}).then(rs => {
      message.success(rs.message)
      this.tableRef.current.reload();
    }).catch(hide)
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
          return http.requestPageData('weapp/page', params, sort)
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
          <Form.Item label='应用Id' name='appId' rules={[{required: true}]}>
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



