import React, {Fragment} from 'react';
import {http, PageContent, ZzTable} from "../../../../common";


const baseApi = 'sysVisLog/';
const pageApi = baseApi + 'page'


export default class extends React.Component {

  columns = [
    {
      title: '日志名称',
      dataIndex: 'name',
    },
    {
      title: '访问时间',
      dataIndex: 'visTime',
      sorter:true
    },
    {
      title: '访问人',
      dataIndex: 'account',
      sorter: true
    },
    {
      title: '是否成功',
      dataIndex: 'success',
      valueType: 'dict',
      params: 'YN'
    },
    {
      title: '消息',
      dataIndex: 'message',
    },
    {
      title: 'ip',
      dataIndex: 'ip'
    },

    {
      title: '位置',
      dataIndex: 'location',
    },
    {
      title: '浏览器',
      dataIndex: 'browser'
    },

  ];


  render() {
    return <PageContent bgGray>
      <ZzTable
        requestUrl={pageApi}
        columns={this.columns}
      />
    </PageContent>
  }


}



