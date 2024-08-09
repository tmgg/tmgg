import React from "react";
import {Button, message, Modal, Popconfirm, Space, Table, Upload} from "antd";
import {http} from "@tmgg/tmgg-base";
import {QuestionOutlined} from "@ant-design/icons";

export default class extends React.Component {


  columns = [
    {
      dataIndex: 'name',
      title: '文件'
    },

    {
      dataIndex: 'id',
      title: '唯一标识'
    },


    {
      dataIndex: 'modifiedDate',
      title: '更新时间'
    },
    {
      dataIndex: 'option',
      title: '-',
      render: (_, record) => {
        return <div>
          <Space>
            <Popconfirm title='确定删除文件?' onConfirm={() => this.deleteFile(record.id)}>
              <Button size='small'>删除</Button>
            </Popconfirm>


          </Space>
        </div>
      }
    }
  ]
  state = {
    list: []
  }

  componentDidMount() {
    this.loadData()
  }

  loadData() {
    http.get('/kettle/file/list').then(rs => {
      if (rs.success) {
        this.setState({list: rs.data})
      } else {
        Modal.error({
          title: '获取存储库失败',
          content: rs.message
        })
      }

    })
  }

  deleteFile = id => {
    http.get('/kettle/file/delete', {id}).then(rs => {
      if (rs.success) {
        message.success(rs.message)
        this.loadData()
      } else {
        message.error(rs.message)
      }
    })
  }


  handleChange = ({fileList, event, file}) => {
    const rs = file.response;
    if (rs != null && rs.success === false) {
      Modal.error({
        title: '上传失败',
        content: rs.message,
      });
      return;
    }

    fileList.forEach((f) => {
      if (f.status === 'done' && f.response && f.response.code == 200) {
        this.loadData()
      }
    });

  };

  render() {
    return <>
      <div style={{display: 'flex', justifyContent: 'end'}}>
        <Upload accept={".kjb,.ktr"}
                onChange={this.handleChange}
                action={'/kettle/file/upload'}
                multiple={true}
        >
          <Button type='primary'>上传</Button>
        </Upload></div>
      <Table columns={this.columns} dataSource={this.state.list} rowKey='id'
             indentSize={24}
             pagination={false}></Table>
    </>
  }
}
