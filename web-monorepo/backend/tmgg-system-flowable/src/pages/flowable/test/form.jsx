import React from "react";
import {Form, Input, Typography} from "antd";
import {PageUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {




    render() {
        console.log('测试表单', this.props)
        return <div>
            <Typography.Title level={3}>测试表单</Typography.Title>
            <Form>
                <Form.Item name='days' label='请假天数'>
                    <Input disabled/>
                </Form.Item>
            </Form>

            路径：{JSON.stringify(PageUtil.currentParams())}
        </div>
    }
}
