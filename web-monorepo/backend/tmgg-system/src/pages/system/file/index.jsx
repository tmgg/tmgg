import {Form, Input, Popconfirm} from 'antd'
import React from 'react'
import {ButtonList, FieldDateRange, HttpUtil, ProTable} from "@tmgg/tmgg-base";


export default class extends React.Component {


  tableRef = React.createRef()

  columns = [
      {
          title: 'id',
          dataIndex: 'id',
      },
    {
      title: '存储位置',
      dataIndex: 'fileLocation',

      valueEnum: {
        1: '阿里云',
        2: '腾讯云',
        3: 'minio',
        4: '本地'
      }
    },
    {
      title: '仓库',
      dataIndex: 'fileBucket',
    },

    {
      title: '名称',
      tooltip: '上传时候的文件名',
      dataIndex: 'fileOriginName',
    },

    {
      title: '后缀',
      dataIndex: 'fileSuffix',
    },

    {
      title: '大小信息',
      dataIndex: 'fileSizeInfo',
    },

    {
      title: '对象名称',
      dataIndex: 'fileObjectName',
      tooltip: '文件唯一标识id'
    },

    {
      title: '存储路径',
      dataIndex: 'filePath',
    },

    {
      title: '创建时间',
      dataIndex: 'createTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      render: (_, record) => (
        <ButtonList>
          <Popconfirm perm='sysFile:delete' title='是否确定删除文件信息' onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>
        </ButtonList>
      ),
    },
  ]


  handleDelete = row => {
    HttpUtil.postForm('sysFile/delete', row).then(rs => {
      this.tableRef.current.reload()
    })
  }


  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
        request={(params) => HttpUtil.pageData('sysFile/page', params)}
        columns={this.columns}
        searchFormItemsRender={()=><>
          <Form.Item label='存储位置' name='fileLocation'>
            <Input />
          </Form.Item>
          <Form.Item label='仓库' name='fileBucket'>
            <Input />
          </Form.Item>
          <Form.Item label='文件名' name='fileOriginName'>
            <Input />
          </Form.Item>
          <Form.Item label='对象名称' name='fileObjectName'>
            <Input />
          </Form.Item>
          <Form.Item label='存储路径' name='filePath'>
            <Input />
          </Form.Item>

          <Form.Item label='文件大小' name='fileSize'>
          </Form.Item>

          <Form.Item label='上传者' name='createUser'>
            <Input />
          </Form.Item>
          <Form.Item label='上传时间' name='dateRange'>
           <FieldDateRange />
          </Form.Item>

        </>}
      />
    </>
  }
}



