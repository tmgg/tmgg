import React from "react";
import {http} from "@tmgg/tmgg-base";
import {Card, Descriptions, Tag} from "antd";
const Item = Descriptions.Item

export default class extends React.Component {

  state ={
    jobStatus: {}
  }

  componentDidMount() {
    const {jobId, jobName} = this.props

    http.get('/kettle/jobStatus', {jobId,jobName}).then(rs=>{
      this.setState({jobStatus:rs.data})
    })
  }

  render() {
    let {jobStatus} = this.state;
    let desc = jobStatus.statusDescription;
    let descColor = desc == 'Finished' ? 'green':'red'
    return <div>

      <Card>
        <Descriptions column={2}>
          <Item label='id'>{jobStatus.id}</Item>
          <Item label='状态'><Tag color={descColor}>{desc}</Tag></Item>
          <Item label='日志时间'>{jobStatus.logDate}</Item>
        </Descriptions>
      </Card>


      <Card title='日志' style={{marginTop:12}}>
        <pre>{jobStatus.loggingString}</pre>
      </Card>

    </div>
  }
}
