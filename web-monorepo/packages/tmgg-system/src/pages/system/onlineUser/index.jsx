import React from 'react';
import {message, Popconfirm} from "antd";
import {ButtonList, HttpUtil} from "@tmgg/tmgg-base";
import {ProTable} from "@tmgg/pro-table";



export default class extends React.Component {


  columns = [
    {
      title: '账号',
      dataIndex: 'account'
    },
    {
      title: '名称',
      dataIndex: 'name'
    },
    {
      title: '最后登录IP',
      dataIndex: 'lastLoginIp'
    },
    {
      title: '最后登录时间',
      dataIndex: 'lastLoginTime'
    },
    {
      title: '最后登录地址',
      dataIndex: 'lastLoginAddress',
    },
    {
      title: '最后登录浏览器',
      dataIndex: 'lastLoginBrowser',
    },
    {
      title: '最后登录所用系统',
      dataIndex: 'lastLoginOs'
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <ButtonList>
          <Popconfirm perm='sysOnlineUser:forceExist' title='是否强制下线该用户？'
                      onConfirm={() => this.forceExist(record)}>
            <a>强制下线</a>
          </Popconfirm>
        </ButtonList>
      ),
    },
  ];

  actionRef = React.createRef();

  forceExist = row => {
    HttpUtil.post('sysOnlineUser/forceExist', row).then(r => {
      this.actionRef.current.reload()
    })
  }


  render() {
    return <ProTable
      ref={this.actionRef}
      request={(params, sort, filter)=>HttpUtil.pageData('sysOnlineUser/page', params, sort)}
      columns={this.columns}
      rowKey="sessionId"
      search={false}
    />
  }


}



