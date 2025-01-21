import React from "react";
import {Card, Form} from "antd";
import {FieldEditor} from "@tmgg/tmgg-base";

export default class extends React.Component {


    render() {

        return <Card title='测试页面'>

            <Form.Item label='x' name='x'>
                <FieldEditor />
            </Form.Item>
        </Card>
    }
}
