import React from "react";
import {Card, Form} from "antd";
import {FieldFileBase64, FieldUploadCropImage, FieldUploadFile} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        info: {
            image:'1965707162522783744,1965707365019586560'
        }
    }

    render() {

        return <Card title='测试页面'>

            {
                JSON.stringify(this.state.info)
            }

            <Form onValuesChange={v=>this.setState({info:v})} initialValues={this.state.info}>
                <Form.Item label='文件剪切上传' name='image' >
                    <FieldUploadFile maxCount={5} />
                </Form.Item>
            </Form>


        </Card>
    }
}
