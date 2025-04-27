import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString, HttpUtil, ProTable} from "@tmgg/tmgg-base";



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
      title: '应用id',
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
      title: 'token',
      dataIndex: 'token',




    },

    {
      title: 'encodingAESKey',
      dataIndex: 'encodingAESKey',




    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
          <ButtonList>
            <Button size='small' perm='weixinMp:save' onClick={() => this.handleEdit(record)}>编辑</Button>
            <Popconfirm perm='weixinMp:delete' title='是否确定删除公众号'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'weixinMp/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.postForm( 'weixinMp/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='weixinMp:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => HttpUtil.pageData('weixinMp/page', params)}
          columns={this.columns}
          /*searchFormItemsRender={() => {
            return <>
              <Form.Item label='名称' name='name'>
                <Input/>
              </Form.Item>
              <Form.Item label='应用id' name='appId'>
                <Input/>
              </Form.Item>
              <Form.Item label='密钥' name='appSecret'>
                <Input/>
              </Form.Item>
              <Form.Item label='备注' name='remark'>
                <Input/>
              </Form.Item>
              <Form.Item label='token' name='token'>
                <Input/>
              </Form.Item>
              <Form.Item label='encodingAESKey' name='encodingAESKey'>
                <Input/>
              </Form.Item>
            </>
          }}*/
      />

      <Modal title='公众号'
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

          <Form.Item label='名称' name='name' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='应用id' name='appId' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='密钥' name='appSecret' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='备注' name='remark' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='token' name='token' >
            <Input/>
          </Form.Item>
          <Form.Item label='encodingAESKey' name='encodingAESKey' >
            <Input/>
          </Form.Item>

        </Form>
      </Modal>
    </>


  }
}

