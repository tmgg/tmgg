import React from "react";
import {Button, Card, Collapse, Form, Input, message, Row, Splitter, Table} from "antd";
import {FieldRemoteTreeSelect, HttpUtil, PageUtil, SysUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {


    formRef = React.createRef()

    state = {
        code: null,
        previewData: {
            keys:[],
            dataList:[]
        }
    }

    componentDidMount() {
        const id = PageUtil.currentLocationQuery().id
        this.id = id
        HttpUtil.get("sysReportTable/get", {id}).then(rs => {

            this.setState({code: rs.code})
            this.formRef.current.setFieldsValue(rs)

            this.viewData()
        })

    }



    viewData = () => {
        const values = this.formRef.current.getFieldsValue();
        this.setState({
            previewData: {
                keys:[],
                dataList:[]
            }})
        if(values.sql){
            const hide = message.loading('加载数据中...', 0)
            HttpUtil.post('sysReportTable/preview', values).then(rs => {
               this.setState({previewData: rs})
            }).finally(hide)
        }
    };

    fillDemo = () => {
        const sql = 'select * from sys_menu ';
        this.formRef.current.setFieldsValue({sql})
    };
    onFinish = values => {
        HttpUtil.post('sysReportTable/save', values).then(rs => {
            this.setState({formOpen: false})
        })
    }



    getUrlCode() {
        return SysUtil.getServerUrl() + "sysReportTable/view/" + this.state.code
    }

    render() {
        return <><Card>
            <Form ref={this.formRef}
                  labelCol={{flex: '100px'}}
                  onValuesChange={(changedValues, values) => {
                      this.setState({form: values})
                  }}
                  onFinish={this.onFinish}
            >
                <Form.Item name='id' noStyle></Form.Item>
                <Row>
                    <Form.Item label='标题' name='title' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='嵌入菜单' name='sysMenuPid'>
                        <FieldRemoteTreeSelect url='sysMenu/menuTree'/>
                    </Form.Item>

                </Row>

                <Form.Item label='sql' name='sql' rules={[{required: true}]}
                           help={<a onClick={this.fillDemo}>填入示例</a>}>
                    <Input.TextArea rows={5}/>
                </Form.Item>

                <div style={{marginLeft: 100, marginTop: 36, display: 'flex', gap: 16}}>
                    <Button type='primary' danger htmlType="submit">保存</Button>
                    <Button type='primary' onClick={this.viewData}>预览数据</Button>
                </div>
            </Form>

        </Card>
            <Card style={{marginTop: 24}}>
                <Splitter>
                    <Splitter.Panel size='70%'>
                        <Table dataSource={this.state.previewData.dataList}
                               size={"small"}
                               columns={this.state.previewData.keys.map(k=>{
                                   return {
                                       title:k,
                                       dataIndex:k
                                   }
                               })}></Table>
                    </Splitter.Panel>
                    <Splitter.Panel>
                        <Collapse
                            style={{margin: 16}}
                            items={[
                                {
                                    key: '1',
                                    label: 'PC端嵌入代码（React）',
                                    children: <pre>  {'<Chart code="' + this.state.code + '" />'}</pre>
                                },
                                {
                                    key: 'api',
                                    label: 'api接口调用',
                                    children: <div>接口地址： <Input
                                        value={'/sysReportTable/getData/' + this.state.code}></Input>
                                    </div>
                                },
                                {
                                    key: '2',
                                    label: 'url直链访问',
                                    children: <div>
                                        <div>{'/sysReportTable/getData/' + this.state.code}</div>
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
