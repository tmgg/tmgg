import React from 'react'
import {ProTable} from "@ant-design/pro-table";
import {http} from "@tmgg/tmgg-base";

export default class extends React.Component {


    columns = [
        {
            title: '应用id',
            dataIndex: 'appId',
        },
        {
            title: 'openId',
            dataIndex: 'openId',
        },
        {
            title: '昵称',
            dataIndex: 'nickName',
        },
        {
            title: '头像',
            dataIndex: 'avatarUrl',
        },
        {
            title: '上次登录时间',
            dataIndex: 'lastLoginTime',
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
        },
    ]


    render() {
        return <div>
            <ProTable
                request={(params, sort) => {
                    return http.requestPageData('weappUser/page', params, sort).then(rs => {
                        return rs;
                    });
                }}
                columns={this.columns}
                rowSelection={false}
                rowKey='id'
                bordered
            />


        </div>
    }
}



