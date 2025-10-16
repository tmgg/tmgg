import React from "react";
import {Button, Card, Form, Input, Radio, Spin, Splitter,} from "antd";
import InstanceInfo from "../../../components/InstanceInfo";
import {HttpUtil, Page, PageUtil} from "@tmgg/tmgg-base"
import {history} from "umi";

export default class extends React.Component {

    state = {
        submitLoading: false,
        taskId: null,
        instanceId: null,
        formKey: null
    }

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const {taskId, instanceId, formKey} = PageUtil.currentParams()
        this.setState({taskId, instanceId, formKey})
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
        if (!instanceId) {
            return <Spin/>
        }
        return <Page padding>

            <Splitter>
                <Splitter.Panel>
                    <InstanceInfo id={instanceId} formKey={this.state.formKey}/>
                </Splitter.Panel>
                <Splitter.Panel defaultSize={400}>
                    <Card title='审批意见'>
                        <Form
                            layout='vertical'
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
                            <Form.Item label='审批意见' name='comment'
                                       rules={[{required: true, message: '请输入审批意见'}]}>
                                <Input.TextArea/>
                            </Form.Item>
                            <div>
                                <Button type='primary' htmlType='submit' loading={submitLoading}
                                        size={"middle"}>提&nbsp;交</Button>
                            </div>
                        </Form>
                    </Card>
                </Splitter.Panel>

            </Splitter>


        </Page>


    }
}
