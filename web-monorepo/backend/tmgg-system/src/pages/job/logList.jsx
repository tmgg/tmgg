import {Button} from 'antd'
import React from 'react'
import {HttpUtil, ProTable, SysUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,

    selectedRowKeys: [],

  }

  tableRef = React.createRef()

  columns = [
    {
      title: '名称',
      dataIndex: ['sysJob','name'],
    },
    {
      title: '执行类',
      dataIndex: ['sysJob','jobClass'],
    },

    {
      title: '开始时间',
      dataIndex: 'beginTime',
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
    },
    {
      title: '耗时',
      dataIndex: 'jobRunTimeLabel',

    },
    {
      title: '结果',
      dataIndex: 'result',
      width:300
    },

    {
      title: '操作',
      dataIndex: 'option',
      fixed:'right',
      render: (_, record) => {
        let url = SysUtil.getServerUrl() + 'job/log/print?jobLogId='+ record.id;
        return <a  href={url} target='_blank'>日志</a>;
      },
    },

  ]

  clean = (selectedRowKeys)=>{
    HttpUtil.post('/job/jobLogClean', {ids:selectedRowKeys} ).then(rs=>{
      this.tableRef.current.reload()
    })
  }



  render() {
    return <>
      <ProTable
        toolBarRender={(_,{selectedRowKeys})=><>
          <Button disabled={selectedRowKeys.length === 0} onClick={()=>this.clean(selectedRowKeys)} type='primary' >删除{selectedRowKeys.length}条</Button>
        </>}
        actionRef={this.tableRef}
        request={(params, sort) => {
          return HttpUtil.pageData('job/jobLog', params, sort);
        }}
        columns={this.columns}
        rowSelection={{}}
        rowKey='id'
      />

    </>
  }
}



