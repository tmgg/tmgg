import {Popconfirm} from 'antd'
import React from 'react'
import {ButtonList, http, HttpUtil, ProTable} from "@tmgg/tmgg-base";


export default class extends React.Component {


  tableRef = React.createRef()

  columns = [

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
      title: '大小kb',
      dataIndex: 'fileSizeKb',
    },

    {
      title: '大小信息',
      dataIndex: 'fileSizeInfo',
    },

    {
      title: '存储到bucket的名称',
      dataIndex: 'fileObjectName',
      tooltip: '文件唯一标识id'


    },

    {
      title: '存储路径',
      dataIndex: 'filePath',
    },

    {
      title: '业务标识',
      dataIndex: 'businessKey',
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
        ref={this.tableRef}
        request={(params) => HttpUtil.pageData('sysFile/page', params)}
        columns={this.columns}
      />
    </>
  }
}



