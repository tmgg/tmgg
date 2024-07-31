import {Card, Descriptions, Empty, Popconfirm, Table, Tabs} from 'antd';
import React from 'react';

import {ProForm, ProFormItem, ProFormText} from "@ant-design/pro-components";
import RoleMenuTree from "./RoleMenuTree";
import RoleUserTree from "./RoleUserTree";
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
      <LeftRightLayout leftSize={500}>
        <Card title='角色列表' extra={
          <ButtonList maxNum={3}>
            <a perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: {}})
              this.formRef.current.show()
            }}>新增</a>
            <a perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: selectData})
              this.formRef.current.show()
            }
            }> 修改 </a>

            <Popconfirm perm={basePerm + 'delete'} title={'是否确定删除'}
                        onConfirm={() => this.handleDelete(selectData.id)}>
              <a>删除</a>
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
              {dataIndex: 'code', title: "角色代码"}
            ]}
            rowKey='id'
            pagination={false}
            bordered
            size="small"
          >

          </Table>


        </Card>

        <Card title='角色信息'>
          {this.renderRoleDetail(selectData)}

        </Card>


      </LeftRightLayout>


      <ProModal title='角色' ref={this.formRef}>
        <ProForm onFinish={this.handleSave} initialValues={this.state.formValues}>
          <ProFormItem name='id' noStyle/>
          <ProFormText label='名称' name='name' rules={[{required: true}]}/>
          <ProFormText label='编码' name='code' rules={[{required: true}]}/>
          <ProFormText label='序号' name='sort' rules={[{required: true}]}/>
        </ProForm>

      </ProModal>


    </>
  }


  renderRoleDetail(selectData) {
    if(selectData == null || !selectData.id){
      return <Empty description='请先选择角色'> </Empty>
    }
    return <>
      <Descriptions>
        <Descriptions.Item label='角色名称'>{selectData.name}</Descriptions.Item>
        <Descriptions.Item label='编码'>{selectData.code}</Descriptions.Item>
        <Descriptions.Item label='序号'>{selectData.sort}</Descriptions.Item>
        <Descriptions.Item label='备注'>{selectData.remark}</Descriptions.Item>
      </Descriptions>
      <Tabs type='card' style={{marginTop: 16}} destroyInactiveTabPane>
        <Tabs.TabPane tab='设置功能权限' key='1'>
          <RoleMenuTree id={selectData.id}/>
        </Tabs.TabPane>
        <Tabs.TabPane tab='设置用户' key='2'>
          <RoleUserTree id={selectData.id}/>
        </Tabs.TabPane>
      </Tabs>
    </>
  }
}



