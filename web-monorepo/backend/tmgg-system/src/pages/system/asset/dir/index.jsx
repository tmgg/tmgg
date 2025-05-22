import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'
import {ButtonList,dictValueTag, ViewBoolean,FieldDateRange,FieldDictSelect,FieldRadioBoolean, FieldDatePickerString, FieldDateTimePickerString, HttpUtil, ProTable} from "@tmgg/tmgg-base";



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
      title: '编码',
      dataIndex: 'code',




    },

    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
          <ButtonList>
            <Button size='small' perm='sysAssetDir:save' onClick={() => this.handleEdit(record)}>编辑</Button>
            <Popconfirm perm='sysAssetDir:delete' title='是否确定删除素材文件夹'  onConfirm={() => this.handleDelete(record)}>
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
    HttpUtil.post( 'sysAssetDir/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.postForm( 'sysAssetDir/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
            toolBarRender={(params, {selectedRows,selectedRowKeys}) => {
            return <ButtonList>
              <Button perm='sysAssetDir:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params) => HttpUtil.pageData('sysAssetDir/page', params)}
          columns={this.columns}
          searchFormItemsRender={() => {
              return <>
                  <Form.Item label='名称' name='name'>
                         <Input/>
                  </Form.Item>
                  <Form.Item label='编码' name='code'>
                         <Input/>
                  </Form.Item>
              </>
          }}
      />

  <Modal title='素材文件夹'
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


