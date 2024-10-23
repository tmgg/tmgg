import React, {Fragment} from 'react';
import {Modal, Tag} from "antd";
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
            title: '类型',
            dataIndex: 'name',
            render(v, record) {
                if (v) {
                    return v;
                }
                return record.url
            }
        },


        {
            title: '时间',
            dataIndex: 'createTime',
        },
        {
            title: '账号',
            dataIndex: 'account'
        },

        {
            title: '结果',
            dataIndex: 'success',
            hideInSearch: true,
            render(v, record) {
                return  <>
                    <Tag color={v ?'green':'red'}>{v ? '成功':'失败'}</Tag>
                    {record.message}
                </>
            }
        },

        {
            title: 'ip定位',
            dataIndex: 'ip',
            hideInSearch: true,
            render(v, record) {
                return <>
                {v} {record.location}
                </>;
            }
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
                request={(params, sort, filter) => HttpUtil.pageData(pageApi, params, sort)}
                columns={this.columns}
                rowKey='id'
            />


        </>
    }


}



