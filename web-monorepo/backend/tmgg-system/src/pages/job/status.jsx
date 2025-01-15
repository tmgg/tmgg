import React from "react";
import {Card, Table} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        dataSource: []
    }

    componentDidMount() {
        HttpUtil.get('jobStatus/list').then(rs => {
            this.setState({dataSource: rs})
        })

    }

    render() {
        return <Card>
            <Table
                bordered
                rowKey='id'
                pagination={false}
                dataSource={this.state.dataSource}
                columns={[
                    {title: '任务', dataIndex: 'name'},
                    {title: '执行类', dataIndex: 'className'},
                    {title: '触发时间', dataIndex: 'fireTime'},
                    {title: '运行时间', dataIndex: 'jobRunTime'}

                ]}></Table>
        </Card>
    }
}
