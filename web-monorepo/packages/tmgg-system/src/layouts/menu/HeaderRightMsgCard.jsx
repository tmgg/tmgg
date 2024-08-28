import React from "react";
import {Badge, Card, Table, Tabs} from "antd";
import {HttpClient} from "../../common";


export default class HeaderRightMsgCard extends React.Component {

    state = {
        data: []
    }

    componentDidMount() {
        HttpClient.get('/getMessageList').then(rs => {
            this.setState({data: rs.data})
        })
    }


    render() {
        const keys = Object.keys(this.state.data)
        const items = keys.map(key => {
            const list = this.state.data[key]
            return {
                key,
                label: <Badge dot={list.length}>{key}</Badge>,
                children: <Table dataSource={list}
                                 columns={[
                                     {title: '标题', dataIndex: 'title'},
                                     {title: '内容', dataIndex: 'content'},
                                     {title: '时间', dataIndex: 'createTime'}
                                 ]}
                                 size={"small"}
                ></Table>
            }
        })


        return <Card style={{padding: '0 16px', width: 400}}>

            <Tabs items={items}/>

        </Card>
    }
}
