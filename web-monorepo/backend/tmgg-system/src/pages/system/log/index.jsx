import React, {Fragment} from 'react';
import {Form, Input, Tag, Typography} from "antd";
import {Ellipsis, FieldDateRange, HttpUtil, ProTable} from "@tmgg/tmgg-base";




export default class extends React.Component {


    columns = [

        {
            title: '模块',
            dataIndex: 'module',
        },
        {
            title: '操作',
            dataIndex: 'name',
        },
        {
            title: '结果',
            dataIndex: 'success',
            render(v, record) {
                return <>
                    <Tag color={v ? 'green' : 'red'}>{v ? '成功' : '失败'}</Tag>
                </>
            }
        },
        {
            title: '消息',
            dataIndex: 'message',
            render(v, record) {
                return <Ellipsis>{v}</Ellipsis>;
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
            title: '账号',
            dataIndex: 'account'
        },
        {
            title: '时间',
            dataIndex: 'createTime',
            sorter: true,
        },
        {
            title: '请求参数',
            dataIndex: 'param',
            render(v, record) {
                return <Typography.Text>{v}</Typography.Text>
            }

        },

    ];


    render() {
        return <>
            <ProTable
                request={(params) => HttpUtil.pageData('sysLog/page', params)}
                columns={this.columns}

                searchFormItemsRender={(formInstance) => {
                    return <>
                        <Form.Item label='模块' name='module'>
                            <Input/>
                        </Form.Item>
                        <Form.Item label='操作' name='name'>
                            <Input/>
                        </Form.Item>
                        <Form.Item label='时间' name='dateRange'>
                            <FieldDateRange format={"YYYY"}/>
                        </Form.Item>
                    </>
                }}
            />

        </>
    }


}



