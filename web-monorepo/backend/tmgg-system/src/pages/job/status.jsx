import React from "react";
import {Alert, Table} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        dataSource: [],
        summary: null
    }

    componentDidMount() {
        HttpUtil.get('jobStatus/info').then(rs => {
            this.setState({dataSource: rs.list,summary:rs.summary})
        })

    }

    render() {
        return <div>
            <Alert message={<pre>{this.state.summary}</pre>} style={{marginBottom:12}}></Alert>
            <Table
                title={()=><div>正在执行的Job</div>}
                bordered
                rowKey='id'
                pagination={false}
                dataSource={this.state.dataSource}
                columns={[
                    {title: '实例ID', dataIndex: 'id'},
                    {title: 'JobKey', dataIndex: 'jobKey'},
                    {title: 'TriggerKey', dataIndex: 'triggerKey'},
                    {title: '执行类', dataIndex: 'className'},
                    {title: '触发时间', dataIndex: 'fireTime'},
                    {title: '下次触发', dataIndex: 'nextFireTime'},

                ]}></Table>
        </div>
    }
}
