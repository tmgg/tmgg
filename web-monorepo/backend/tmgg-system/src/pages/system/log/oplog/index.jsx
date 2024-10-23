import React, {Fragment} from 'react';
import {Modal, Tag} from "antd";
import {ProTable} from "@tmgg/pro-table";
import {Ellipsis, HttpUtil} from "@tmgg/tmgg-base";
import {DateUtil} from "@tmgg/tmgg-commons-lang";


const baseApi = 'sysOpLog/';

const pageApi = baseApi + 'page'

export default class extends React.Component {

    state = {
        showDetail: false,
        formValues: {}
    }

    columns = [
        {
            title: '账号',
            dataIndex: 'account'
        },
        {
            title: '时间',
            dataIndex: 'createTime',
            render(v, record) {
                return DateUtil.friendlyTime(v);
            }
        },

        {
            title: '操作',
            dataIndex: 'name',

        },



        {
            title: '结果',
            dataIndex: 'success',
            hideInSearch: true,
            render(v, record) {
                return  <>
                    <Tag color={v ?'green':'red'}>{v ? '成功':'失败'}</Tag>
                </>
            }
        },
        {
            title: '消息',
            dataIndex: 'message',
            hideInSearch: true,
            render(v, record) {
                return <Ellipsis >{v}</Ellipsis>;
            }
        },

        {
            title: 'ip',
            dataIndex: 'ip',
            hideInSearch: true,

        },
        {
            title: '定位',
            dataIndex: 'location',
            hideInSearch: true,

        },
        {
            title: '浏览器',
            dataIndex: 'browser',
            hideInSearch: true,
        },

        {
            title: '操作系统',
            dataIndex: 'os',
            hideInSearch: true,
        },

        {
            title: 'url',
            dataIndex: 'url',
            hideInSearch: true,

        },
        {
            title: '请求参数',
            dataIndex: 'param',
            hideInSearch: true,
            render(v, record) {
                return <Ellipsis >{v}</Ellipsis>;
            }

        },
    ];


    render() {
        return <>
            <ProTable
                request={(params, sort, filter) => HttpUtil.pageData(pageApi, params, sort)}
                columns={this.columns}
                rowKey='id'
                scroll={{x:'max-content'}}
            />


        </>
    }


}



