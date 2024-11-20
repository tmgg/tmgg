import React, {Fragment} from "react";
import {ProTable} from "@tmgg/tmgg-base";
import InstanceInfo from "./InstanceInfo";
import {Modal} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class MyInstance extends React.Component {

    state = {
        instance: {}
    }

    actionRef = React.createRef()
    columns = [

        {
            title: '流程名称',
            dataIndex: 'processDefinitionName',
            render(_, r) {
                return r.name || r.processDefinitionName
            }
        },
        {
            title: '发起人',
            dataIndex: 'startUserName',
        },
        {
            title: '发起时间',
            dataIndex: 'startTime',
        },
        {
            title: '业务标识',
            dataIndex: 'businessKey',

        },


        {
            title: '结束时间',
            dataIndex: 'endTime',
        },

        {
            title: '流程状态',
            dataIndex: 'x',
            render(_, row) {
                return row.endTime == null ? '进行中' : '已结束'
            }
        },
        {
            title: '终止原因',
            dataIndex: 'deleteReason',
        },


        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <a onClick={() => {
                    this.setState({instance: record})

                  Modal.info({
                    title:'流程信息',
                    icon: null,
                    closeable:true,
                    content: <InstanceInfo id={this.state.instance.id}/>
                  })
                }}> 查看 </a>
            ),
        },
    ];



    render() {
        return <>
            <ProTable
                search={false}
                actionRef={this.actionRef}
                request={(params, sort) => HttpUtil.pageData("flowable/userside/myInstance", params, sort)}
                columns={this.columns}
                rowSelection={false}
                rowKey="id"
            />

        </>


    }


}
