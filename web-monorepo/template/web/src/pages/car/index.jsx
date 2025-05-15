import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, InputNumber, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ButtonList, FieldRadioBoolean, HttpUtil, ProTable} from '@tmgg/tmgg-base'


export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [

    {
      title: '车牌号',
      dataIndex: 'carNo',


    },

    {
      title: '车型',
      dataIndex: 'type',


    },

    {
      title: '颜色',
      dataIndex: 'color',


    },

    {
      title: '可容纳人数',
      dataIndex: 'capacity',


    },

    {
      title: '启用',
      dataIndex: 'enabled',


    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
          <ButtonList>
            <a perm='car:save' onClick={() => this.handleEdit(record)}> 编辑 </a>
            <Popconfirm perm='car:delete' title='是否确定删除车辆'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'car/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.postForm( 'car/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='car:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(jobParamDescs, sort) => HttpUtil.pageData('car/page', jobParamDescs, sort)}
          columns={this.columns}
          rowKey='id'
      />

  <Modal title='车辆'
    open={this.state.formOpen}
    onOk={() => this.formRef.current.submit()}
    onCancel={() => this.setState({formOpen: false})}
    destroyOnHidden
    maskClosable={false}
    >

    <Form ref={this.formRef} labelCol={{flex: '100px'}}
        initialValues={this.state.formValues}
        onFinish={this.onFinish}

        >
        <Form.Item  name='id' noStyle></Form.Item>

              <Form.Item label='车牌号' name='carNo' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='车型' name='type' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='颜色' name='color' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='可容纳人数' name='capacity' rules={[{required: true}]}>
                    <InputNumber />
              </Form.Item>
              <Form.Item label='启用' name='enabled' rules={[{required: true}]}>
                   <FieldRadioBoolean />
              </Form.Item>

    </Form>
  </Modal>
    </>


  }
}



