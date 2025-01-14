import React from "react";
import {Button, Card, Input, Space, Tabs} from "antd";

export default class extends React.Component {
    render() {
        return <Card title='sql图表生成' extra={<Space>
            <Button type='primary' >保存</Button>
        </Space>}>


            <Input.TextArea placeholder='请输入SQL文本，按回车键预览' style={{margin: '10px 0'}}/>

            <Tabs items={[
                {
                    key: '1', label: '图表（Java生成图片）',
                },
                {
                    key: '2', label: '图表（echarts）',
                },

                {
                    key: 'dataset', label: '查询结果'
                }
            ]}></Tabs>

        </Card>
    }
}
