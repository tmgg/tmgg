import React from "react";
import {Button, message, Modal, Popconfirm, Space, Table, Upload} from "antd";
import {http} from "@tmgg/tmgg-base";
import {QuestionOutlined} from "@ant-design/icons";

export default class extends React.Component {


  columns = [
    {
      dataIndex: 'fileName',
      title: '文件名'
    },
    {
      dataIndex: 'fileType',
      title: '类型',
      render(v) {
        return {
          kjb:'作业',
          ktr: '转换'
        }[v] || v;
      }
    },
    {
      dataIndex: 'name',
      title: '名称'
    },
    {
      dataIndex: 'description',
      title: '描述'
    },
    {
      dataIndex: 'parameterList',
      title: '参数',
      render(v){
        return JSON.stringify(v,' ',2)
      }
    },
    {
      dataIndex: 'updateTime',
      title: '上传时间'
    },
    {
      dataIndex: 'option',
      title: '-',
      render: (_, record) => {
        return <div>
          <Space>
            <Popconfirm title='确定删除文件?' onConfirm={()=>this.deleteFile(record.id)}>
              <Button size='small' >删除</Button>
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
      this.setState({list: rs.data})
    })
  }

  deleteFile = id => {
    http.get('/kettle/file/delete', {id}).then(rs => {
      message.success(rs.message)
      this.loadData()
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
      <Table columns={this.columns} dataSource={this.state.list} rowKey='id' pagination={false}></Table>





    </>
  }
}
