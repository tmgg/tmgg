import React from "react";
import {Button, Card, Form, Input, Radio, Select} from "antd";
import * as echarts from 'echarts';
import {HttpUtil, PageUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {


    formRef = React.createRef()

    componentDidMount() {
        const id = PageUtil.currentLocationQuery().id
        HttpUtil.get("sysChart/get", {id}).then(rs => {
            if (!rs.type) {
                rs.type = 'bar'
            }
            this.formRef.current.setFieldsValue(rs)
        })

    }

    onRef = dom => {
        this.clean()

        if (dom) {
            const myChart = echarts.init(dom);
            this.myChart = myChart;
        }
    }

    componentWillUnmount() {
        this.clean();
    }

    clean() {
        if (this.myChart) {
            this.myChart.dispose();
        }
    }

    chartRender = () => {
        const values = this.formRef.current.getFieldsValue();
        HttpUtil.post('sysChart/preview', values).then(rs => {
            this.myChart.setOption(rs, true);
        })

    };
    fillDemo = () => {
        const sql = 'select type, count(*) 数量, max(seq) 最大序号 from sys_menu group by type';
        this.formRef.current.setFieldsValue({sql})
    };
    onFinish = values => {
        HttpUtil.post('sysChart/save', values).then(rs => {
            this.setState({formOpen: false})
        })
    }


    render() {
        return <><Card>
            <Form ref={this.formRef} labelCol={{flex: '100px'}} onValuesChange={(changedValues, values) => {
                this.setState({form: values})
            }}
            onFinish={this.onFinish}
            >
                <Form.Item name='id' noStyle></Form.Item>
                <Form.Item label='标题' name='title' rules={[{required:true}]}>
                    <Input/>
                </Form.Item>
                <Form.Item label='类型' name='type' rules={[{required:true}]}>
                    <Radio.Group options={[
                        {
                            label: '柱状图', value: 'bar'
                        },
                        {
                            label: '折线图', value: 'line'
                        },
                        {
                            label: '普通饼图', value: 'pie'
                        }
                    ]}></Radio.Group>
                </Form.Item>
                <Form.Item label='sql' name='sql' rules={[{required:true}]} help={<a onClick={this.fillDemo}>填入示例</a>}>
                    <Input.TextArea/>
                </Form.Item>




                <div style={{marginLeft: 100, marginTop:36, display: 'flex', gap: 16}}>

                    <Button type='primary' danger htmlType="submit">保存</Button>
                    <Button type='primary' onClick={this.chartRender}>预览</Button>

                </div>
            </Form>

        </Card>
            <Card style={{marginTop: 24}}>
                <div ref={this.onRef}
                     style={{width: '100%', height: 500, marginTop: 36}}></div>


            </Card>
        </>
    }


}
