import React from "react";
import {Button, Card, Collapse, Form, Input, Radio, Splitter} from "antd";
import * as echarts from 'echarts';
import {HttpUtil, PageUtil, SysUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {


    formRef = React.createRef()

    state = {
        code: null
    }

    componentDidMount() {
        const id = PageUtil.currentLocationQuery().id
        this.id = id
        HttpUtil.get("sysChart/get", {id}).then(rs => {
            if (!rs.type) {
                rs.type = 'bar'
            }
            this.setState({code: rs.code})
            this.formRef.current.setFieldsValue(rs)

            this.chartRender()
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


    getPCcode() {
        return '<Chart code="' + this.state.code + '" />'
    }

    getUrlCode() {
        return SysUtil.getServerUrl() + "sysChart/view/" + this.state.code
    }

    render() {
        return <><Card>
            <Form ref={this.formRef} labelCol={{flex: '100px'}} onValuesChange={(changedValues, values) => {
                this.setState({form: values})
            }}
                  onFinish={this.onFinish}
            >
                <Form.Item name='id' noStyle></Form.Item>
                <Form.Item label='标题' name='title' rules={[{required: true}]}>
                    <Input/>
                </Form.Item>
                <Form.Item label='类型' name='type' rules={[{required: true}]}>
                    <Radio.Group options={[
                        {
                            label: '柱状图', value: 'bar'
                        },
                        {
                            label: '折线图', value: 'line'
                        },
                        {
                            label: '饼图', value: 'pie'
                        }
                    ]}></Radio.Group>
                </Form.Item>
                <Form.Item label='sql' name='sql' rules={[{required: true}]}
                           help={<a onClick={this.fillDemo}>填入示例</a>}>
                    <Input.TextArea/>
                </Form.Item>

                <div style={{marginLeft: 100, marginTop: 36, display: 'flex', gap: 16}}>
                    <Button type='primary' danger htmlType="submit">保存</Button>
                    <Button type='primary' onClick={this.chartRender}>预览</Button>
                </div>
            </Form>

        </Card>
            <Card style={{marginTop: 24}}>
                <Splitter>
                    <Splitter.Panel size='70%'>
                        <div ref={this.onRef}
                             style={{width: '100%', height: 500, marginTop: 36}}></div>
                    </Splitter.Panel>
                    <Splitter.Panel>
                        <Collapse
                            style={{margin:16}}
                            items={[
                                {
                                    key: '1',
                                    label: 'PC端嵌入代码（React）',
                                    children: <pre>  {this.getPCcode()}</pre>
                                },
                                {
                                    key: 'api',
                                    label: 'api接口调用',
                                    children: <div>接口地址： <Input
                                        value={'/sysChart/getOption/' + this.state.code}></Input>
                                    </div>
                                },
                                {
                                    key: '2',
                                    label: 'url直链访问',
                                    children: <div>
                                        <div>{'/sysChart/getOption/' + this.state.code}</div>
                                        <a href={this.getUrlCode()} target="_blank">点击打开</a>
                                    </div>
                                },
                                {
                                    key: '3',
                                    label: '小程序嵌入',
                                    children: 'echarts-for-weixin 项目提供了一个小程序组件，用这种方式可以方便地使用 ECharts。'
                                },
                                {
                                    key: 'img',
                                    label: '图片直链',
                                    children: '开发中...'
                                },
                            ]}
                            defaultActiveKey='1'/>


                    </Splitter.Panel>
                </Splitter>

            </Card>
        </>
    }


}