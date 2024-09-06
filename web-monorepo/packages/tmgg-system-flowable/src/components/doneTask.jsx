import React from "react";
import {ProTable} from "@tmgg/pro-table";
import InstanceInfo from "./InstanceInfo";
import {HttpUtil, ProModal} from "@tmgg/tmgg-base";

export default class  extends React.Component {

    state = {
        task: {}
    }

    actionRef = React.createRef()
    formRef = React.createRef()
    columns = [
        {
            title: '流程名称',
            dataIndex: 'instanceName',
        },
        {
            title: '发起人',
            dataIndex: 'instanceStarter'
        },
        {
            title: '发起时间',
            dataIndex: 'instanceStartTime',
        },
        {
            title: '任务创建时间',
            dataIndex: 'createTime',
        },
        {
            title: '处理时间',
            dataIndex: 'endTime',
        },
        {
            title: '耗时(小时)',
            dataIndex: 'durationInHours',
        },
        {
            title: '处理节点',
            dataIndex: 'taskName'
        },
        {
            title: '操作人',
            dataIndex: 'assigneeInfo'
        },

        {
            title: '流程状态',
            dataIndex: 'instanceStatusLabel'
        },


        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <a onClick={() => {
                    this.setState({task: record})
                    this.formRef.current.show(record)
                }}> 查看 </a>
            ),
        },
    ];

    onFinish = () => {
        this.actionRef.current.reload()
        this.formRef.current.hide()
    }

    render() {
        const {instanceId} = this.state.task

        return <>
            <ProTable
                search={false}
                actionRef={this.actionRef}
                request={(params, sort) => HttpUtil.pageData("flowable/userside/doneTaskPage", params, sort)}
                columns={this.columns}
                rowSelection={false}
                rowKey="id"
                size='small'
            />


            <ProModal actionRef={this.formRef} title='查看流程' width={800}>
                <InstanceInfo id={instanceId}/>
            </ProModal>

        </>


    }


}
