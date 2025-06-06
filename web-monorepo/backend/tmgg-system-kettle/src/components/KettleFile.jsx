import React from "react";
import {Button, message, Modal, Popconfirm, Space, Table, Tabs, Upload} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

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
        const isFile = record.id.endsWith(".kjb") || record.id.endsWith(".ktr")
        return <div>
          <Space>

            <Button disabled={!isFile} size='small' onClick={() => {
              this.setState({file: record, detailOpen: true});
              this.loadXml(record.id)
            }}>查看</Button>

            <Popconfirm title='确定删除文件?' onConfirm={() => this.deleteFile(record.id)}>
              <Button size='small'>删除</Button>
            </Popconfirm>
          </Space>
        </div>
      }
    }
  ]
  state = {
    list: [],
    detailOpen: false,
    file: null,
    xml: null
  }

  componentDidMount() {
    this.loadData()
  }

  loadData() {
    HttpUtil.get('/kettle/file/list').then(rs => {
        this.setState({list: rs})
    })
  }

  deleteFile = id => {
    HttpUtil.postForm('/kettle/file/delete', {id}).then(rs => {
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
      if (f.status === 'done' && f.response && f.response.code == 0) {
        this.loadData()
      }
    });

  };

  loadXml = ( id) => {
    HttpUtil.get('/kettle/xml', {id}).then(rs => {
      this.setState({xml:rs})
    })
  }

  render() {
    return <>
      <div style={{display: 'flex', justifyContent: 'end'}}>
        <Upload accept={".kjb,.ktr"}
                onChange={this.handleChange}
                action={'/kettle/file/upload'}
                multiple={true}
        >
          <Button type='primary' >上传作业文件</Button>
        </Upload>
      </div>

      <Table columns={this.columns} dataSource={this.state.list} rowKey='id'
             indentSize={24}
             pagination={false}

      ></Table>


      <Modal title='文件详情'
             open={this.state.detailOpen}
             footer={null} width='80vw'
             destroyOnHidden={true}
             onCancel={() => this.setState({detailOpen: false})}>

        <div style={{overflow:"auto",maxHeight: 500}}>
          <pre contentEditable={true}>{this.state.xml}</pre>
        </div>

      </Modal>
    </>
  }
}
