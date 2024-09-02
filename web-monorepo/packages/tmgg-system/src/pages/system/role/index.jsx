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
  message
} from 'antd';
import React from 'react';

import RoleMenuTree from "./RoleMenuTree";
import {ButtonList, http, LeftRightLayout} from "@tmgg/tmgg-base";

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
    httpUtil.get(baseApi + 'page').then(rs => {
      const list = rs.data;
      this.setState({roleList: list})
    })
  }



  handleSave = value => {
    httpUtil.post(baseApi + 'save', value).then(rs => {
      message.success(rs.message)
      this.setState({formOpen:false,curRecord:rs.data})
      this.loadData()
    })
  }


  handleDelete = (id) => {
    httpUtil.get(baseApi + 'delete', {id}).then(rs => {
      this.loadData()
    })
  }




  render() {
    return <>
    <LeftRightLayout leftSize={600}>
        <Card  extra={
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

        <Card title='角色信息'>
          {this.renderRoleDetail()}

        </Card>


      </LeftRightLayout>


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



