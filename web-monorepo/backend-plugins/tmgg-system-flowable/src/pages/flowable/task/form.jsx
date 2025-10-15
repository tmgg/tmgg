import React from "react";
import {Button, Card, Form, Input, Modal, Radio, Spin,} from "antd";
import InstanceInfo from "../../../components/InstanceInfo";
import {HttpUtil, Page, PageUtil} from "@tmgg/tmgg-base"
import {history} from "umi";

export default class extends React.Component {

  state = {
    submitLoading: false,
    taskId: null,
    instanceId:null,
    formKey:null
  }

  constructor(props) {
    super(props);
  }

  componentDidMount() {
    const {taskId,instanceId,formKey} = PageUtil.currentParams()
    this.setState({taskId,instanceId,formKey})
  }

  handleTask = value => {
    this.setState({submitLoading: true});
    value.taskId = this.state.taskId
    HttpUtil.post("/flowable/userClient/handleTask", value).then(rs => {
       history.replace('/flowable/task')
    }).finally(() => {
      this.setState({submitLoading: false})
    })

  }

  render() {
    const {submitLoading} = this.state
    const instanceId = this.state.instanceId
    if(!instanceId){
      return <Spin />
    }
    return <Page padding>

      <InstanceInfo id={instanceId} formKey={this.state.formKey}/>

      <Card style={{marginTop: 12}} title='审批'>

        <Form
          labelCol={{flex: '100px'}}
          onFinish={this.handleTask}
          disabled={submitLoading}
        >
          <Form.Item label='审批结果' name='result' rules={[{required: true, message: '请选择'}]}
                     initialValue={'APPROVE'}>
            <Radio.Group>
              <Radio value='APPROVE'>同意</Radio>
              <Radio value='REJECT'>不同意</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label='审批意见' name='comment' rules={[{required: true, message: '请输入审批意见'}]}>
            <Input.TextArea/>
          </Form.Item>

          <Form.Item label='  ' colon={false}>
            <Button type='primary' htmlType='submit' loading={submitLoading} size={"large"}>确定</Button>
          </Form.Item>
        </Form>
      </Card>
    </Page>


  }
}
