import {
  Form,
  Modal,
  Card,
  Descriptions,
  Empty,
  Input,
  Popconfirm,
  Table,
  Button,
  message, Splitter
} from 'antd';
import React from 'react';

import RoleMenuTree from "./RoleMenuTree";
import {ButtonList,  HttpUtil} from "@tmgg/tmgg-base";

const baseApi = 'sysRole/';
const basePerm = 'sysRole:';


export default class extends React.Component {


  formRef = React.createRef()


  state = {
    selectedRowKeys:[],
    formOpen:false,
    curRecord: {},
    roleList: []
  }

  componentDidMount() {
    this.loadData()
  }

  loadData = () => {
    HttpUtil.get(baseApi + 'page').then(rs => {
      const list = rs;
      this.setState({roleList: list})
    })
  }



  handleSave = value => {
    HttpUtil.post(baseApi + 'save', value).then(rs => {
      this.setState({formOpen:false,curRecord:rs})
      this.loadData()
    })
  }


  handleDelete = (id) => {
    HttpUtil.post(baseApi + 'delete', null,{id}).then(rs => {
      this.loadData()
    })
  }




  render() {
    return <>
    <Splitter >
      <Splitter.Panel defaultSize={600}>
        <Card  title='角色列表' extra={
          <ButtonList maxNum={3}>
            <Button  type='primary' perm={basePerm + 'save'} onClick={() => {
              this.setState({formOpen:true},()=>{
                this.formRef.current.resetFields()

              })
            }}>新增</Button>
            <Button  perm={basePerm + 'save'} onClick={() => {
              this.setState({formOpen:true},()=>{
                this.formRef.current.setFieldsValue(this.state.curRecord)
              })

            }
            }> 修改 </Button>

            <Popconfirm perm={basePerm + 'delete'} title={'是否确定删除'}
                        onConfirm={() => this.handleDelete(this.state.curRecord.id)}>
              <Button >删除</Button>
            </Popconfirm>
          </ButtonList>
        }>


          <Table
            dataSource={this.state.roleList}
            columns={[
              {dataIndex: 'name', title: "角色名称"},
              {dataIndex: 'code', title: "角色代码"},
              {dataIndex: 'remark', title: "备注"},

            ]}
            rowKey='id'
            pagination={false}

            rowSelection={{
              type: 'radio',
              selectedRowKeys: this.state.selectedRowKeys,
              onChange: (selectedRowKeys,selectedRows)=>{
                this.setState({curRecord:selectedRows[0],selectedRowKeys: selectedRowKeys})
              }
            }}
            onRow={(record) => ({
              onClick: () => {
                this.setState({curRecord:record,selectedRowKeys: [record.id]})
              }
            })}
          >

          </Table>


        </Card>
      </Splitter.Panel>
      <Splitter>
        <Card title='角色信息'>
          {this.renderRoleDetail()}
        </Card>
      </Splitter>

      </Splitter>


      <Modal open={this.state.formOpen}
             title='角色信息'
             onOk={()=>this.formRef.current.submit()}
             onCancel={()=>this.setState({formOpen:false})}
      >
        <Form ref={this.formRef} onFinish={this.handleSave}  labelCol={{flex:"100px"}}>
          <Form.Item name='id' noStyle/>
          <Form.Item label='名称' name='name' rules={[{required: true}]}>
            <Input />
          </Form.Item>
          <Form.Item label='编码' name='code' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='备注' name='remark' >
            <Input/>
          </Form.Item>

        </Form>

      </Modal>


    </>
  }


  renderRoleDetail() {
    const {curRecord} = this.state;
    if(curRecord == null || !curRecord.id){
      return <Empty description='请先选择角色'> </Empty>
    }
    return <>
      <Descriptions column={2}>
        <Descriptions.Item label='角色名称'>{curRecord.name}</Descriptions.Item>
        <Descriptions.Item label='编码'>{curRecord.code}</Descriptions.Item>
        <Descriptions.Item label='序号'>{curRecord.sort}</Descriptions.Item>
        <Descriptions.Item label='备注'>{curRecord.remark}</Descriptions.Item>
      </Descriptions>

      <h4>权限设置</h4>
      <RoleMenuTree id={curRecord.id}/>
    </>
  }
}



