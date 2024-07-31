import {Button, Form, Input, message, Modal, Popconfirm, Switch} from "antd";
import {ProTable} from "@ant-design/pro-components";
import {HttpClient} from "../../../common";
import React from "react";

export default class extends React.Component {

  columns = [
    {title: '页面路径', dataIndex: 'path'},
    {title: '是否启用', dataIndex: 'enable', valueType: 'boolean'},
    {
      dataIndex: 'options',
      render:(_,record)=> {
        return <Popconfirm title='确定删除该条记录？' onConfirm={()=>this.delete(record.id)}>
          <a>删除</a>
        </Popconfirm>
      }
    },
  ]

  state = {
    formOpen: false,
    formValues: {}
  }

  tableRef = React.createRef();
  formRef = React.createRef()

  delete = (id) => {
    HttpClient.get('watermark/delete', {id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  submit = (values) => {
    HttpClient.post('watermark/save', values).then(rs => {
      message.success(rs.message)
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
        toolBarRender={() => <Button type='primary' onClick={() => this.setState({
          formOpen: true,
          formValues: {enable: true}
        })}>新增</Button>}
        actionRef={this.tableRef}
        columns={this.columns}
        request={(params, sort) => {
          return HttpClient.getPageableData('watermark/page', params, sort)
        }}
        search={false}

        rowKey='id'
      >
      </ProTable>


      <Modal title='水印信息'
             open={this.state.formOpen}
             destroyOnClose
             onOk={() => {
               this.formRef.current.submit();
             }}
             onCancel={() => this.setState({formOpen: false})}
      >
        <Form ref={this.formRef}
              initialValues={this.state.formValues}
              onFinish={this.submit}
              layout='horizontal'
              labelCol={{flex: '100px'}}>
          <Form.Item name='id' noStyle></Form.Item>
          <Form.Item name='path' label='页面路径' rules={[{required: true}]} tooltip='如 /system/watermark'>
            <Input/>
          </Form.Item>
          <Form.Item name='enable' label='是否启用' rules={[{required: true}]} valuePropName='checked'>
            <Switch/>
          </Form.Item>

        </Form>
      </Modal>
    </>
  }
}
