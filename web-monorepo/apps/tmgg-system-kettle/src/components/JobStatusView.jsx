import React from "react";
import {http} from "@tmgg/tmgg-base";
import {Alert, Card, Descriptions, Tag} from "antd";

const Item = Descriptions.Item

export default class extends React.Component {

  state = {
    jobStatus: {},
    jobImageErrorMessage: null,
    jobImageUrl: null
  }

  componentDidMount() {
    const {jobId, jobName} = this.props

    http.get('/kettle/jobStatus', {jobId, jobName}).then(rs => {
      this.setState({jobStatus: rs.data})
    })

    http.get('/kettle/jobImage', {jobId, jobName}).then(rs => {
      let jobImageErrorMessage = null;
      let jobImageUrl = null;

      if (rs.success) {
        jobImageUrl = 'data:image/png;base64,' + rs.data;
      } else {
        jobImageErrorMessage = rs.message;
      }
      this.setState({jobImageUrl})
      this.setState({jobImageErrorMessage})
    })


  }

  render() {
    let {jobStatus} = this.state;
    let desc = jobStatus.statusDescription;
    let descColor = desc == 'Finished' ? 'green' : 'red'
    return <div>

      <Card>
        <Descriptions column={2}>
          <Item label='id'>{jobStatus.id}</Item>
          <Item label='状态'><Tag color={descColor}>{desc}</Tag></Item>
          <Item label='日志时间'>{jobStatus.logDate}</Item>
        </Descriptions>
      </Card>


      <Card title='状态图' style={{marginTop: 12}}>
        {this.state.jobImageErrorMessage && <Alert message={this.state.jobImageErrorMessage}></Alert>}

        <img src={this.state.jobImageUrl} width='100%'/>
      </Card>


      <Card title='日志' style={{marginTop: 12}}>
        <pre>{jobStatus.loggingString}</pre>
      </Card>

    </div>
  }
}
