import {Button, Input, InputNumber, Modal} from 'antd'
import React from 'react'
import StreamLog from "../../components/StreamLog";
import {ProTable} from "@tmgg/tmgg-base";
import {HttpUtil, SysUtil} from "@tmgg/tmgg-base";




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
      valueType: 'datetime'
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      valueType: 'datetime'
    },
    {
      title: '耗时',
      dataIndex: 'jobRunTimeLabel',

    },
    {
      title: '结果',
      dataIndex: 'result',
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

  clean = (cleanDate)=>{
    HttpUtil.postForm('/job/jobLogClean', {cleanDate} ).then(rs=>{
      this.tableRef.current.reload()
    })
  }



  render() {
    return <>
      <ProTable
        toolBarRender={()=><>
          <Button onClick={()=>this.clean(1)}>清理</Button>
        </>}
        actionRef={this.tableRef}
        request={(params, sort) => {
          return HttpUtil.pageData('job/jobLog', params, sort);
        }}
        columns={this.columns}
        rowSelection={false}
        rowKey='id'
      />

    </>
  }
}


