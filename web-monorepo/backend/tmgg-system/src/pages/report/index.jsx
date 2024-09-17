import {PlusOutlined} from '@ant-design/icons'
import {Button, Popconfirm} from 'antd'
import React from 'react'
import {ProTable} from "@ant-design/pro-components";
import {ButtonList, HttpUtil,  ProModal} from "@tmgg/tmgg-base";




export default class extends React.Component {

  state = {
    formValues: {},
  }

  tableRef = React.createRef()
  addRef = React.createRef()
  editRef = React.createRef()

  columns = [

    {
      title: '名称',
      dataIndex: 'name',
      sorter: true,


    },

    {
      title: '内容',
      dataIndex: 'content',
      sorter: true,


    },

    {
      title: '排序',
      dataIndex: 'seq',
      sorter: true,

       valueType: 'digit',

    },



    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
          <ButtonList>
            <a perm='sysReport:save' onClick={() => this.handleEdit(record)}> 修改 </a>
            <Popconfirm perm='sysReport:delete' title='是否确定删除系统报表'  onConfirm={() => this.handleDelete(record)}>
              <a>删除</a>
            </Popconfirm>
          </ButtonList>
      ),
    },
  ]

  handleAdd = ()=>{
    this.addRef.current.show()
  }
  handleSave = value => {
    HttpUtil.post( 'sysReport/save', value).then(rs => {
      this.addRef.current.hide()
      this.tableRef.current.reload()
    })
  }

  handleEdit = record=>{
    this.editRef.current.show()
    this.setState({formValues: record})
  }
  handleUpdate = value => {
    let params = {id:this.state.formValues.id, ...value}
    HttpUtil.post('sysReport/save', params).then(rs => {
      this.editRef.current.hide()
      this.tableRef.current.reload()
    })
  }

  handleDelete = row => {
    HttpUtil.post( 'sysReport/delete', row).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='sysReport:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(params, sort) => HttpUtil.pageData('sysReport/page', params, sort)}
          columns={this.columns}
          rowSelection={false}
          rowKey='id'
          columnEmptyText={false}
          bordered
          scroll={{
            x: 'max-content',
          }}
      />

      <ProModal actionRef={this.addRef} title='新增系统报表'>
        <ProTable
            onSubmit={this.handleSave}
            type='form'
            columns={this.columns}
        />
      </ProModal>

      <ProModal actionRef={this.editRef} title='编辑系统报表'>
        <ProTable
            onSubmit={this.handleUpdate}
            form={{initialValues: this.state.formValues}}
            type='form'
            columns={this.columns}
        />
      </ProModal>

    </>
  }
}



