import React, {Fragment} from 'react';
import {Modal} from "antd";
import {ProTable} from "@tmgg/pro-table";
import {HttpUtil} from "@tmgg/tmgg-base";


const baseApi = 'sysOpLog/';

const pageApi = baseApi + 'page'

export default class extends React.Component {

    state = {
        showDetail: false,
        formValues: {}
    }

    columns = [
        {
            title: '日志名称',
            dataIndex: 'name',
        },


        {
            title: '请求地址',
            dataIndex: 'url',
        },
        {
            title: '操作时间',
            dataIndex: 'opTime',
        },
        {
            title: '操作人',
            dataIndex: 'account'
        },

        {
            title: '是否成功',
            dataIndex: 'success',
            valueType: 'boolean',
        },
        {
            title: 'ip',
            dataIndex: 'ip'
        },
        {
            title: '地址',
            dataIndex: 'location',
        },
        {
            title: '浏览器',
            dataIndex: 'browser',
        },
        {
            title: '操作系统',
            dataIndex: 'os',
        },
        {
            title: '类名称',
            dataIndex: 'className',
            hideInTable: true
        },
        {
            title: '方法名称',
            dataIndex: 'methodName',
            hideInTable: true
        },
        {
            title: '消息',
            dataIndex: 'message',
            hideInTable: true
        },
        {
            title: '请求参数',
            dataIndex: 'param',
            valueType: "jsonCode",
            hideInTable: true
        },
        {
            title: '返回结果',
            dataIndex: 'result',
            valueType: "jsonCode",
            hideInTable: true
        },

        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, r) => {
                return <a onClick={() => this.view(r)}>查看</a>
            }
        },

    ];

    view = (r) => {
        this.setState({showDetail: r, formValues: r})
    }


    render() {
        return <>
            <ProTable
                request={(params, sort, filter)=>HttpUtil.pageData(pageApi,params, sort)}
                columns={this.columns}
                rowKey='id'
            />


        </>
    }


}



