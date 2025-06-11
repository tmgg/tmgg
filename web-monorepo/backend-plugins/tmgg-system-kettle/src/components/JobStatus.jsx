import React from "react";
import {Button, Modal, Table, Tag} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";
import JobStatusView from "./JobStatusView";
import {ReloadOutlined} from "@ant-design/icons";

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
      render(desc) {
        let descColor = desc == 'Finished' ? 'green' : 'red';
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
    loading: false,
    list: [],
    jobId: null,
    jobName: null,
    viewOpen: false
  }

  componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    this.setState({loading: true})
    HttpUtil.get('/kettle/status').then(rs => {
      this.setState({list: rs?.jobStatusList})
      this.setState({loading: false})
    })
  };

  render() {
    return <>
      <div style={{display: 'flex', justifyContent: 'end'}}>
        <Button icon={<ReloadOutlined/>}
                onClick={this.loadData}></Button>
      </div>

      <Table columns={this.columns} dataSource={this.state.list} rowKey={'id'} pagination={false} loading={this.state.loading}></Table>
      <Modal width={'80vw'} title='作业状态' destroyOnHidden
             open={this.state.viewOpen}
             onOk={()=>this.setState({viewOpen:false})}
             onCancel={() => this.setState({viewOpen: false})}
              footer={null}
      >
        <div style={{height:'60vh', overflow:'auto', padding:12}}>
          <JobStatusView jobId={this.state.jobId} jobName={this.state.jobName}/>
        </div>
      </Modal>
    </>
  }
}
