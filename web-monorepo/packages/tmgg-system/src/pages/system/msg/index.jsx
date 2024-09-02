import React from "react";
import {Button, Card, Tabs, Tag} from "antd";
import {ProTable} from "@ant-design/pro-components";
import {http} from "../../../common";

export default class  extends React.Component {

  tableRef = React.createRef()

  columns = [
    {
      title: '时间',
      dataIndex: 'createTimeLabel',
      width: 200
    },
    {
      title: '标题',
      dataIndex: 'title',
    },
    {
      title: '内容',
      dataIndex: 'content',
      render(content, row) {
        return <div dangerouslySetInnerHTML={{__html: content}}/>
      }
    },
    {
      title: '状态',
      dataIndex: 'read',
      render: (read, row) => {
        if (read == true) {
          return <Tag color='green'>{row.readTimeLabel}已读</Tag>
        } else {
          return <div><Button size='small' onClick={() => this.read(row)}> 标记为已读 </Button></div>
        }
      }
    }
  ]

  read = (record) => {
    http.post("/msg/read", record).then(rs => {
      this.tableRef.current.reload()
    })
  }



  render() {
    return <Card>

      <Tabs defaultActiveKey="0" destroyInactiveTabPane tabBarExtraContent={<a onClick={()=>this.tableRef.current.reload()}>刷新</a>}>
        <Tabs.TabPane key='0' tab='所有消息'>
          <ProTable
            actionRef={this.tableRef}
            request={(params, sort) => http.getPageableData("msg/page", params, sort)}
            columns={this.columns}
            rowSelection={false}
            rowKey='id'
            search={false}
            options={false}
            size='small'
          />
        </Tabs.TabPane>
        <Tabs.TabPane key='1' tab='未读消息'>
          <ProTable
            actionRef={this.tableRef}
            request={(params, sort) => http.getPageableData("msg/page?read=false", params, sort)}
            columns={this.columns}
            rowSelection={false}
            rowKey='id'
            search={false}
            options={false}
            size='small'
          />


        </Tabs.TabPane>
        <Tabs.TabPane key='2' tab='已读消息'>

          <ProTable
            actionRef={this.tableRef}
            request={(params, sort) => http.getPageableData("msg/page?read=true", params, sort)}
            rowSelection={false}
            rowKey='id'
            search={false}
            options={false}
            size='small'
            columns={this.columns}
          />


        </Tabs.TabPane>

      </Tabs>

    </Card>
  }
}
