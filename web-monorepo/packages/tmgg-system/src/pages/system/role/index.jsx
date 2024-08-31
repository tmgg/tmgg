import {Form, Card, Descriptions, Empty, Input, Popconfirm, Table, Tabs, Button, Typography} from 'antd';
import React from 'react';

import {ProForm, ProFormItem, ProFormText} from "@ant-design/pro-components";
import RoleMenuTree from "./RoleMenuTree";
import {ButtonList, HttpClient, LeftRightLayout, ProModal} from "../../../common";

const baseApi = 'sysRole/';
const basePerm = 'sysRole:';


export default class extends React.Component {


  formRef = React.createRef()


  state = {
    formValues: {},
    dataMap: {},
    selectData: {},
    roleList: []
  }

  componentDidMount() {
    this.loadData()
  }

  loadData = () => {
    HttpClient.get(baseApi + 'page').then(rs => {
      const list = rs.data;
      this.setState({roleList: list})

      const dataMap = {}

      list.map(t => {
        dataMap[t.id] = t;
      })
      this.setState({dataMap})
    })
  }
  handleSave = value => {
    HttpClient.post(baseApi + 'save', value).then(rs => {
      this.formRef.current.close()
      this.loadData()
    })
  }


  handleDelete = (id) => {
    HttpClient.post(baseApi + 'delete', {id}).then(rs => {
      this.loadData()
    })
  }

  render() {
    let {selectData} = this.state

    return <>
      <LeftRightLayout leftSize={600}>
        <Card  extra={
          <ButtonList maxNum={3}>
            <Button size='small' type='primary' perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: {}})
              this.formRef.current.show()
            }}>新增</Button>
            <Button size='small' perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: selectData})
              this.formRef.current.show()
            }
            }> 修改 </Button>

            <Popconfirm perm={basePerm + 'delete'} title={'是否确定删除'}
                        onConfirm={() => this.handleDelete(selectData.id)}>
              <Button size='small'>删除</Button>
            </Popconfirm>
          </ButtonList>
        }>


          <Table
            rowSelection={{
              type: 'radio', onSelect: (data) => {
                this.setState({selectData:null},()=>{
                  this.setState({selectData: data})
                })
              }
            }}
            dataSource={this.state.roleList}
            columns={[
              {dataIndex: 'name', title: "角色名称"},
              {dataIndex: 'code', title: "角色代码"},
              {dataIndex: 'remark', title: "备注"},

            ]}
            rowKey='id'
            pagination={false}
            bordered
          >

          </Table>


        </Card>

        <Card title='角色信息'>
          {this.renderRoleDetail(selectData)}

        </Card>


      </LeftRightLayout>


      <ProModal title='角色信息' ref={this.formRef}>
        <Form onFinish={this.handleSave} initialValues={this.state.formValues} labelCol={{flex:"100px"}}>
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

      </ProModal>


    </>
  }


  renderRoleDetail(selectData) {
    if(selectData == null || !selectData.id){
      return <Empty description='请先选择角色'> </Empty>
    }
    return <>
      <Descriptions column={2}>
        <Descriptions.Item label='角色名称'>{selectData.name}</Descriptions.Item>
        <Descriptions.Item label='编码'>{selectData.code}</Descriptions.Item>
        <Descriptions.Item label='序号'>{selectData.sort}</Descriptions.Item>
        <Descriptions.Item label='备注'>{selectData.remark}</Descriptions.Item>
      </Descriptions>

      <h4>权限设置</h4>
      <RoleMenuTree id={selectData.id}/>
    </>
  }
}



