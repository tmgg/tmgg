import {Card, Descriptions, Tabs} from 'antd'
import React from 'react'
import WeApp from "./WeApp";
import User from "./User";


export default class extends React.Component {


    render() {
        return <>

            <Card>
                微信小程序应用管理
                <Descriptions>
                    <Descriptions.Item label='小程序个数'></Descriptions.Item>
                    <Descriptions.Item label='用户个数'></Descriptions.Item>
                </Descriptions>
            </Card>

            <div style={{margin: 12}}></div>

            <Card>
                <Tabs items={[
                    {label: '小程序管理', key: 'app', children: <WeApp/>},
                    {label: '用户列表', key: 'user', children: <User/>},
                ]}
                      destroyInactiveTabPane
                ></Tabs></Card>
        </>
    }
}



