import React, {Fragment} from 'react';
import {Form, Tag} from "antd";
import {FieldDatePickerString, FieldDateRange, ProTable} from "@tmgg/tmgg-base";
import {Ellipsis, HttpUtil} from "@tmgg/tmgg-base";
import {DateUtil} from "@tmgg/tmgg-commons-lang";


const baseApi = 'sysOpLog/';

const pageApi = baseApi + 'page'

export default class extends React.Component {


    columns = [


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
        },
        {
            title: '定位',
            dataIndex: 'location',
        },

        {
            title: 'url',
            dataIndex: 'url',
        },
        {
            title: '请求参数',
            dataIndex: 'param',
            hideInSearch: true,
            render(v, record) {
                return <Ellipsis >{v}</Ellipsis>;
            }

        },
        {
            title: '账号',
            dataIndex: 'account'
        },
        {
            title: '时间',
            dataIndex: 'createTime'

        },
    ];


    render() {
        return <>
            <ProTable
                request={(params) => HttpUtil.pageData(pageApi, params)}
                columns={this.columns}
                searchFormItemsRender={(formInstance)=>{
                    return <>
                        <Form.Item label='时间' name='dateRange'>
                            <FieldDateRange />
                        </Form.Item>
                    </>
                }}
            />

        </>
    }


}



