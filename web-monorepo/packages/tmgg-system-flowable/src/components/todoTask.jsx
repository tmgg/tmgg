import React from "react";
import {ProTable} from "@ant-design/pro-components";

export default class extends React.Component {

  state = {
    task: null
  }

  actionRef = React.createRef()
  columns = [

    {
      title: '发起人',
      dataIndex: 'instanceStarter'
    },
    {
      title: '流程名称',
      dataIndex: 'instanceName',
    },
    {
      title: '当前节点',
      dataIndex: 'taskName'
    },
    {
      title: '当前操作人',
      dataIndex: 'assigneeInfo'
    },

    {
      title: '发起时间',
      dataIndex: 'instanceStartTime',
    },
    {
      title: '任务创建时间',
      dataIndex: 'createTime',
    },
    {
      title: '接收时间',
      dataIndex: 'createTime',
    },


    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <a href={'taskForm.html?taskId=' + record.id + '&instanceId=' + record.instanceId}>处理</a>
      ),
    },
  ];


  render() {
    return <ProTable
      search={false}
      actionRef={this.actionRef}
      request={(params, sort) => httpUtil.getPageable("flowable/userside/todoTaskPage", params, sort)}
      columns={this.columns}
      rowSelection={false}
      rowKey="id"
      size='small'
    />
  }


}
