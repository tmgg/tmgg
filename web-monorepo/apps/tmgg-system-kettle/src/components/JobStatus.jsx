import React from "react";
import {Button, Modal, Table, Tag} from "antd";
import {http} from "@tmgg/tmgg-base";
import JobStatusView from "./JobStatusView";

export default class extends React.Component {

  columns = [
    {
      dataIndex: 'id',
      title: 'id'
    },
    {
      dataIndex: 'jobName',
      title: '作业名称'
    },
    {
      dataIndex: 'statusDescription',
      title: '状态描述',
      render(desc){
        let descColor = desc == 'Finished' ? 'green':'red';
        return <Tag color={descColor}>{desc}</Tag>
      }
    },
    {
      dataIndex: 'logDate',
      title: '日志时间'
    },

    {
      dataIndex: 'id',
      title: '-',
      render: (_, record) => {
        const {id, jobName} = record
        return <Button size='small' onClick={() => {
          this.setState({
            jobId: id,
            jobName,
            viewOpen: true
          })

        }}>详情</Button>
      }
    }
  ]

  state = {
    list: [],
    jobId: null,
    jobName: null,
    viewOpen: false
  }

  componentDidMount() {
    http.get('/kettle/status').then(rs => {
      this.setState({list: rs.data?.jobStatusList})
    })
  }

  render() {
    return <>
      <Table columns={this.columns} dataSource={this.state.list} rowKey={'id'} pagination={false}></Table>
      <Modal width={728} title='作业状态' destroyOnClose open={this.state.viewOpen}
             onCancel={() => this.setState({viewOpen: false})}>
        <JobStatusView jobId={this.state.jobId} jobName={this.state.jobName}/>
      </Modal>
    </>
  }
}
