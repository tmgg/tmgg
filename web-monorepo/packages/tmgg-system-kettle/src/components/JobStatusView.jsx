import React from "react";
import {http, HttpUtil} from "@tmgg/tmgg-base";
import {Alert, Card, Descriptions, Tabs, Tag} from "antd";

const Item = Descriptions.Item

export default class extends React.Component {

  state = {
    jobStatus: {},
    jobImageErrorMessage: null,
    jobImageUrl: null
  }

  componentDidMount() {
    const {jobId, jobName} = this.props

    HttpUtil.get('/kettle/jobStatus', {jobId, jobName}).then(rs => {
      this.setState({jobStatus: rs})
    })

    HttpUtil.get('/kettle/jobImage', {jobId, jobName}).then(rs => {
      let jobImageErrorMessage = null;
      let jobImageUrl = null;

        jobImageUrl = 'data:image/png;base64,' + rs.data;
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
          <Item label='作业名称'>{jobStatus.jobName}</Item>
          <Item label='状态'><Tag color={descColor}>{desc}</Tag></Item>
          <Item label='日志时间'>{jobStatus.logDate}</Item>
        </Descriptions>
      </Card>



      <Card style={{marginTop:12}}>

        <Tabs items={[
          {label:"日志",key:'log', children:<pre>{jobStatus.loggingString}</pre>},
          {label:"设计图",key:'img', children:<div>{this.state.jobImageErrorMessage && <Alert message={this.state.jobImageErrorMessage}></Alert>}
              <img src={this.state.jobImageUrl} width='100%'/></div>},
        ]}></Tabs>

      </Card>



    </div>
  }
}
