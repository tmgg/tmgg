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
                            通过其他方式发送
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


                <Title level={4}>1.2 公共请求参数 </Title>

                <Table columns={this.columns} bordered dataSource={[
                    {name: 'action', type: 'String', required: true, desc: '接口定义'},
                    {name: 'appId', type: 'String', required: true, desc: '账号标识,appId'},
                    {name: 'timestamp', type: 'String', required: true, desc: '时间戳'},
                    {name: 'signature', type: 'String', required: true, desc: '数据签名，参考签名算法'},
                    {name: 'data', type: 'String', required: true, desc: '请求参数转JSON后加密'},
                ]} size='small' pagination={false}>
                </Table>

                <Title level={4}>1.3 公共返回参数 </Title>
                <Table columns={this.columns} bordered dataSource={[
                    {name: 'code', type: 'int', required: true, desc: '返回码,成功返回0，其他表示操作错误'},
                    {name: 'msg', type: 'String', required: false, desc: '返回码说明'},
                    {name: 'data', type: 'String', required: false, desc: '返回数据，加密JSON'}
                ]} size='small' pagination={false}>
                </Table>

                <Title level={3}>1.4 签名算法 （signature字段）</Title>
                <Paragraph>
                    <div>
                        1、 将请求参数转换为json得到data字段。
                    </div>
                    <div>
                        2、然后将拼接字符串 action, appId , timestamp , data, 得到待签名内容。
                    </div>
                    <div>
                        3、使用hmacSha256算法，appSecret为秘钥，进行签名，得到 signature
                    </div>
                </Paragraph>

                <Title level={3}>1.5 加密、解密 （data字段）</Title>
                <Title level={4}>加密</Title>
                <Paragraph>
                    <div>
                        1、请求参数转换为JSON结构,得到data
                    </div>
                    <div>
                        2、使用aes算法，appSecret为秘钥，加密并转换为hex字符串，得到最终data
                    </div>
                </Paragraph>
                <Title level={4}>解密</Title>
                <Paragraph>
                    使用aes算法，appSecret为秘钥，用来解密
                </Paragraph>


                <Title level={3}>1.6 SDK</Title>
                <Paragraph>
                    如果是Java项目，可直接使用 java版sdk, 当前版本：{this.state.frameworkVersion}
                    <pre>
                    {`
    <dependency>
        <groupId>io.github.tmgg</groupId>
        <artifactId>tmgg-openapi-sdk</artifactId>
        <version>x.x.x</version>
    </dependency>`
                    }
                    </pre>
                    示例代码：
                    <pre>
                        {`
    public static void main(String[] args) throws IOException {
        //  初始化
        String url = "http://127.0.0.1:8002";
        String appId = "473428cdaeb44e27bb0f45c32a7fc2b5";
        String appSecret = "cbCaGVuSWWgSExaZTvcVG80IrKKVifI2";
        OpenApiSdk sdk = new OpenApiSdk(url, appId, appSecret);

        // 请求接口
        String action = "server.time";
        Map<String, Object> params = new HashMap<>();
        params.put("format", "yyyy-MM-dd HH:mm:ss");
        String result = sdk.send(action, params);
        
        System.out.println("响应的json数据为："+ result);
    }
                        `}
                    </pre>
                </Paragraph>


                <Typography.Title level={2}>2 接口列表</Typography.Title>
                {apiList.map((api, index) => {
                    return <>
                        <Typography.Title level={3}>{'2.' + (index + 1) + " " + api.name}</Typography.Title>
                        <p>{api.desc}</p>

                        <Title level={5}>请求参数</Title>
                        <Table columns={this.columns} bordered dataSource={api.parameterList}
                               size='small' pagination={false}>
                        </Table>

                        <Title level={5}>返回数据</Title>
                        <Typography.Text>
                            返回值类型：{api.returnType}
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
