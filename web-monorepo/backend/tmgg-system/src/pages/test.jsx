import React from "react";
import {Card, Form, Select} from "antd";
import {FieldEditor, FieldEditTable, FieldRemoteSelect} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        info: {}
    }

    render() {

        return <Card title='测试页面'>
            {JSON.stringify(this.state.info)}

            <Form onValuesChange={(changedValues, values) => {
                this.setState({info: values})
            }}>
                <Form.Item label='可编辑表格' name='ports' initialValue={[{port1:'11'}]}>
                    <FieldEditTable columns={[
                        {dataIndex: 'port1', title: '容器端口', align: 'center'},
                        {dataIndex: 'port2', title: '主机端口', align: 'center'},
                        {
                            dataIndex: 'type', title: '协议', align: 'center', render(v) {
                                return <Select value={v}
                                               options={[{label: 'TCP', value: 'tcp'},
                                                   {label: "UDP", value: 'udp'}]}></Select>
                            }
                        },
                    ]}/>

                </Form.Item>
            </Form>


        </Card>
    }
}
