import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, ViewBoolean,FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString,Page, HttpUtil, ProTable} from "@tmgg/tmgg-base";



export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [

    {
      title: '接口名称',
      dataIndex: 'name',




    },

    {
      title: '接口',
      dataIndex: 'action',




    },

      {
          title: 'requestId',
          dataIndex: 'requestId',

      },

    {
      title: '请求数据',
      dataIndex: 'requestData',




    },

    {
      title: '响应数据',
      dataIndex: 'responseData',




    },

    {
      title: 'ip',
      dataIndex: 'ip',




    },

    {
      title: 'ipLocation',
      dataIndex: 'ipLocation',




    },

    {
      title: '执行时间',
      dataIndex: 'executionTime',




    },

    {
      title: '接口账户',
      dataIndex: 'accountName',




    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
          <ButtonList>
            <Button size='small' perm='apiAccessLog:save' onClick={() => this.handleEdit(record)}>编辑</Button>
            <Popconfirm perm='apiAccessLog:delete' title='是否确定删除接口访问记录'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'apiAccessLog/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.postForm( 'apiAccessLog/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <Page>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='apiAccessLog:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params) => HttpUtil.pageData('apiAccessLog/page', params)}
          columns={this.columns}
      />

  <Modal title='接口访问记录'
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

              <Form.Item label='接口名称' name='name' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='接口' name='action' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='请求数据' name='requestData' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='响应数据' name='responseData' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='ip' name='ip' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='ipLocation' name='ipLocation' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='执行时间' name='executionTime' rules={[{required: true}]}>
                    <InputNumber />
              </Form.Item>
              <Form.Item label='接口账户' name='accountName' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>

    </Form>
  </Modal>
    </Page>


  }
}


