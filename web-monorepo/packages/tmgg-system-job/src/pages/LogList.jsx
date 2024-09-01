import {Button, InputNumber, Modal} from 'antd'
import React from 'react'
import StreamLog from "../components/StreamLog";
import {http} from "@tmgg/tmgg-base";
import ProTable from "@tmgg/pro-table";




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
      hideInSearch:true,
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      hideInSearch:true,

    },
    {
      title: '耗时',
      dataIndex: 'jobRunTimeLabel',
      hideInSearch:true,

    },
    {
      title: '结果',
      dataIndex: 'result',
      hideInSearch:true,
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
                content:<StreamLog url={'/job/log/'+ record.id } />
              })
            }}>日志</a>
        );
      },
    },

  ]

  clean = ()=>{
    hutool.http.postForm('/job/jobLogClean', {cleanDate: this.state.cleanDate} ).then(rs=>{
      this.tableRef.current.reload()
    })
  }



  render() {
    return <>
      <ProTable
        toolBarRender={()=>[<>
          清理 <InputNumber style={{width:50}} value={this.state.cleanDate} onChange={v=>this.setState({cleanDate:v})}  /> 天前的日志
          <Button onClick={this.clean}>清理</Button>
        </>]}
        actionRef={this.tableRef}
        request={(params, sort) => {
          return http.pageData('job/jobLog', params, sort, 'POST');
        }}
        columns={this.columns}
        rowSelection={false}
        rowKey='id'
      />

    </>
  }
}



