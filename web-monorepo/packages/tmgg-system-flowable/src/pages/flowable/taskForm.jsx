import React from "react";
import {Button, Card, Form, Input, Modal, Radio,} from "antd";
import InstanceInfo from "../../components/InstanceInfo";
import {HttpUtil, UrlUtil} from "@tmgg/tmgg-base"

export default class extends React.Component {

  state = {
    submitLoading: false,
  }


  handleTask = value => {
    this.setState({submitLoading: true});
    const {taskId} = UrlUtil.params();


    value.taskId = taskId
    HttpUtil.post("/flowable/userside/handleTask", value).then(rs => {
      if(rs.success){
        Modal.success({
          title:'操作结果',
          content: rs.message,
          closable:false,
          okText:'查看我的任务',
          onOk: ()=>{
            window.location.href= "task.html"
          }
        })
      }

    }).finally(() => {
      this.setState({submitLoading: false})
    })

  }

  render() {
    const {submitLoading} = this.state
    let params = UrlUtil.params()

    const {instanceId} = params;
    return <div style={{background: '#f5f5f5'}}>

      <InstanceInfo id={instanceId}/>

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
    </div>


  }
}