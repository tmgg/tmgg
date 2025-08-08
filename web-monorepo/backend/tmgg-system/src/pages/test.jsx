import React from "react";
import {Card, Form} from "antd";
import { FieldFileBase64, FieldUploadCropImage} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        info: {}
    }

    render() {

        return <Card title='测试页面'>


            <Form>
                <Form.Item label='xx' name='image'>
                </Form.Item>
            </Form>


        </Card>
    }
}
