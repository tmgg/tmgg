import React from "react";
import {HttpUtil, PageUtil} from "@tmgg/tmgg-base";
import {Button, Descriptions, Table, Typography} from "antd";

const {Title, Paragraph, Text, Link} = Typography;

export default class extends React.Component {

    state = {
        url: null,
        appId: null,
        apiList: [],
        frameworkVersion: null,

    }

    columns = [
        {dataIndex: 'name', title: '名称', width: 150},
        {dataIndex: 'type', title: '类型', width: 100},
        {
            dataIndex: 'required', title: '必填', width: 100, render: v => {
                if (v == null) {
                    return null;
                }
                return v ? '是' : '否';
            }
        },
        {dataIndex: 'desc', title: '描述'},
        {dataIndex: 'demo', title: '示例值', width: 150}
    ]

    componentDidMount() {
        const id = PageUtil.currentParams().id
        this.setState({appId: id})

        HttpUtil.get('openApiAccount/docInfo', {id}).then(rs => {
            this.setState({apiList: rs.apiList, frameworkVersion: rs.frameworkVersion})
        })

        let url = window.location.protocol + '//' + window.location.host
        this.setState({url})
    }

    print() {
        window.print();
    }

    render() {
        const {apiList} = this.state
        return <div style={{padding: 24}}>
            <Button type='primary' onClick={this.print} className='no-print'>打印文档</Button>
            <iframe id="iframe1" style={{display: 'none'}}></iframe>

            <div id='doc-content'>
                <Title level={1}>开发接口说明文档</Title>

                <Title level={2}>接口数据</Title>
                <Paragraph>
                    <Descriptions column={1} bordered size='small'>
                        <Descriptions.Item label='请求地址'>
                            {this.state.url}/openApi/gateway
                        </Descriptions.Item>
                        <Descriptions.Item label='appId'>
                            {this.state.appId}
                        </Descriptions.Item>
                        <Descriptions.Item label='appSecret'>
                            ******
                        </Descriptions.Item>
                    </Descriptions>

                </Paragraph>


                <Title level={2}>1 接口说明</Title>
                <Title level={3}>1.1 接口协议说明</Title>
                <Paragraph>
                    <Typography.Text>
                        <div>
                            请求使用HTTP POST发送
                        </div>
                        <div> 响应报文以JSON方式返回</div>
                    </Typography.Text>
                </Paragraph>


                <Title level={4}>1.2 公共请求头 </Title>

                <Table columns={this.columns} bordered dataSource={[
                    {name: 'x-action', type: 'String', required: true, desc: '接口定义'},
                    {name: 'x-app-id', type: 'String', required: true, desc: '账号标识,appId'},
                    {name: 'x-timestamp', type: 'String', required: true, desc: '时间戳'},
                    {name: 'x-signature', type: 'String', required: true, desc: '数据签名，参考签名算法'},
                    {name: 'x-request-id', type: 'String', required: true, desc: '请求唯一标识，便于追踪，建议使用uuid'},
                ]} size='small' pagination={false}>
                </Table>

                <Title level={4}>1.3 公共返回参数 </Title>
                <Table columns={this.columns} bordered dataSource={[
                    {name: 'code', type: 'int', required: true, desc: '返回码,成功返回0，其他表示操作错误'},
                    {name: 'message', type: 'String', required: false, desc: '返回码说明'},
                    {name: 'data', type: 'String', required: false, desc: '返回数据JSON'}
                ]} size='small' pagination={false}>
                </Table>

                <Title level={3}>1.4 签名算法 （signature字段）</Title>
                <Paragraph>
                    <div>
                        1、将请求参数按key排序后组装为请求体body  , 如 a=1&b=2&c=3
                    </div>
                    <div>
                        2、然后将拼接字符串 uri, appId , timestamp , body, 中间使用"\n"连接， 得到待签名内容。

                        <div>代码示例：
                        <code>
                        String signStr = uri + "\n" + appId + "\n" + timestamp + "\n" + body;
                        </code>
                        </div>
                    </div>
                    <div>
                        3、使用hmacSha256算法，appSecret为秘钥，进行签名，得到 signature
                    </div>

                </Paragraph>



                <Typography.Title level={2}>2 接口列表</Typography.Title>
                {apiList.map((api, index) => {
                    return <>
                        <Typography.Title level={3}>{'2.' + (index + 1) + " " + api.name} </Typography.Title>
                        <p>uri: {api.uri}</p>
                        <p>{api.desc}</p>



                        <Title level={5}>请求参数</Title>
                        <Table columns={this.columns} bordered dataSource={api.parameterList}
                               size='small' pagination={false}>
                        </Table>

                        <Title level={5}>返回参数</Title>
                        <Typography.Text>
                            返回对象：{api.returnType}
                        </Typography.Text>

                        {api.returnList != null && api.returnList.length > 0 &&
                            <Table columns={this.columns} bordered
                                   dataSource={api.returnList} size='small' pagination={false}>
                            </Table>}

                    </>
                })}

            </div>
        </div>
    }
}
