import {Button, InputNumber, Modal} from 'antd'
import React from 'react'
import StreamLog from "../../components/StreamLog";
import {ProTable} from "@tmgg/tmgg-base";
import {HttpUtil, SysUtil} from "@tmgg/tmgg-base";




export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,

    selectedRowKeys: [],

    cleanDate: 10,
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
      valueType: 'option',
      render: (_, record) => {
        return (
            <a  onClick={()=>{
              Modal.info({
                title:'日志',
                icon:null,
                width:1024,
                closable:true,
                content:<StreamLog url={SysUtil.getServerUrl() + 'job/log/print?jobLogId'+ record.id } />
              })
            }}>日志</a>
        );
      },
    },

  ]

  clean = ()=>{
    HttpUtil.postForm('/job/jobLogClean', {cleanDate: this.state.cleanDate} ).then(rs=>{
      this.tableRef.current.reload()
    })
  }



  render() {
    return <>
      <ProTable
        toolBarRender={()=><>
          清理 <InputNumber style={{width:50}} value={this.state.cleanDate} onChange={v=>this.setState({cleanDate:v})}  /> 天前的日志
          <Button onClick={this.clean}>清理</Button>
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



