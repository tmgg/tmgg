import React from "react";
import {Card, Form} from "antd";
import {FieldEditor, FieldRemoteSelect} from "@tmgg/tmgg-base";

export default class extends React.Component {


    render() {

        return <Card title='测试页面'>

            <Form>
                <Form.Item label='选择角色' name='roleId'>
                    <FieldRemoteSelect url={'/sysRole/options'} style={{width: 200}}/>
                </Form.Item>
            </Form>
        </Card>
    }
}
