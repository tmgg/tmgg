import React from "react";
import {Button, Card, Descriptions} from "antd";
import {Gap, HttpUtil, Page} from "@tmgg/tmgg-base";

const Item = Descriptions.Item;
export default class extends React.Component {

    state = {
        sending: false,

        requestTime: null,
        responseTime: null,

        responseInfo: {
        },
    }

    componentDidMount() {
        this.send()
    }

    send = () => {
        this.setState({sending: true})
        this.setState({requestTime: new Date().getTime()})

        HttpUtil.get('/requestTest/get', {arg1: 'hello', arg2: '姜涛'}).then(rs => {
            this.setState({responseInfo: rs})
            this.setState({responseTime: new Date().getTime()})
        })
    };

    render() {
        let info = this.state.responseInfo;
        let keys = Object.keys(info)

        return <Page padding backgroundGray>
            <Button type='primary' onClick={this.send}>GET</Button>
            <Gap/>
            <Card>
                <Descriptions title='客户端' column={1} bordered labelStyle={{width: 200}} size='small'>
                    <Item label='请求时间'>{this.state.requestTime}</Item>
                    <Item label='接收时间'>{this.state.responseTime}</Item>
                    <Item label='耗时'>{this.state.responseTime - this.state.requestTime} ms</Item>
                </Descriptions>
                <Gap/>

                <Descriptions title='服务端信息' bordered labelStyle={{width: 200}} column={1} size='small'>
                    {keys.map(k => <Item key={k} label={k}>                    {this.renderValue(info[k]   )}                 </Item>)}
                </Descriptions>
            </Card>
        </Page>
    }

    renderValue(v){
        console.log(v)
            return <div dangerouslySetInnerHTML={{__html: v}}></div>
    }
}
