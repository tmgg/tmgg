import React from "react";
import {HttpUtil, ProTable} from "@tmgg/tmgg-base";

export default class extends React.Component {


    render() {
        return <ProTable
            columns={[
                {dataIndex: 'name', title: '名称'},
                {dataIndex: 'action', title: 'action'},
                {dataIndex: 'desc', title: '描述'},

            ]}
            request={(params,) => HttpUtil.pageData('openApiResource/page', params)}
        />
    }
}
