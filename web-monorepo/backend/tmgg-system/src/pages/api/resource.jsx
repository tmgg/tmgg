import React from "react";
import {HttpUtil, ProTable} from "@tmgg/tmgg-base";

export default class extends React.Component {


    render() {
        return <ProTable
            columns={[
                {dataIndex: 'name', title: '名称'},
                {dataIndex: 'uri', title: '路径'},
                {dataIndex: 'desc', title: '描述'},

            ]}
            request={(params,) => HttpUtil.pageData('apiResource/page', params)}
        />
    }
}
