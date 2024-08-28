import React from 'react';
import {message, Popconfirm} from "antd";
import {ButtonList, HttpClient, ZzTable} from "../../../common";



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
    HttpClient.post('sysOnlineUser/forceExist', row).then(r => {
      message.info(r.message || '强制下线成功')
      this.actionRef.current.reload()
    })
  }


  render() {
    return <ZzTable
      ref={this.actionRef}
      requestUrl= 'sysOnlineUser/page'
      columns={this.columns}
      rowKey="sessionId"
      search={false}
    />
  }


}



