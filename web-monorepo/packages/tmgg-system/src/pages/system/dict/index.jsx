import {Button,Card, Descriptions, Empty, Popconfirm, Tree} from 'antd';
import React, {Fragment} from 'react';

import Data from "./Data";
import {ProForm, ProFormItem, ProFormText} from "@ant-design/pro-components";
import {ButtonList, HttpClient, LeftRightLayout, ProModal} from "../../../common";

const baseApi = 'sysDictType/';
const basePerm = 'sysDictType:';


export default class extends React.Component {


  formRef = React.createRef()


  state = {
    formValues: {},
    treeData: [],
    dataMap: {},
    selectData: {}
  }

  componentDidMount() {
    this.loadData()
  }

  loadData = () => {
    HttpClient.get(baseApi + 'page').then(rs => {
      const list = rs.data;

      const dataMap = {}

      const treeData = list.map(t => {
        dataMap[t.id] = t;
        return {title: t.name, key: t.id}
      })


      this.setState({treeData, dataMap})
    })
  }
  handleSave = value => {
    HttpClient.post(baseApi + 'save', value).then(rs => {
      this.formRef.current.close()
      this.loadData()
    })
  }


  handleDelete = () => {
    HttpClient.post(baseApi + 'delete', this.state.formValues).then(rs => {
      this.loadData()
    })
  }

  render() {
    let {selectData} = this.state

    return <>

      <LeftRightLayout leftSize={300}>
        <Card title={'字典类型'} extra={
          <ButtonList>
            <Button type='primary' perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: {}})
              this.formRef.current.show()
            }}>新增</Button>
            <Button perm={basePerm + 'save'} onClick={() => {
              this.setState({formValues: selectData})
              this.formRef.current.show()
            }
            }> 修改 </Button>

            <Popconfirm perm={basePerm + 'delete'} title={'是否确定删除'} onConfirm={this.handleDelete}>
              <a>删除</a>
            </Popconfirm>
          </ButtonList>
        }>

          <Tree
            treeData={this.state.treeData}
            multiple={false}
            showLine={true}
            showIcon={true}
            onSelect={selectedKeys => {
              if (selectedKeys.length > 0) {
                const id = selectedKeys[0];
                this.setState({selectData: this.state.dataMap[id]})
              }
            }
            }></Tree>
        </Card>
        <Card title='字典数据'>
          {selectData?.id ? <div>
            <Descriptions>
              <Descriptions.Item label={'类型名称'}>{selectData.name}</Descriptions.Item>
              <Descriptions.Item label={'唯一编码'}>{selectData.code}</Descriptions.Item>
              <Descriptions.Item label={'序号'}>{selectData.sort}</Descriptions.Item>
              <Descriptions.Item label={'备注'}>{selectData.remark}</Descriptions.Item>

            </Descriptions>
            <Data id={selectData.id}/>
          </div> : <Empty/>}
        </Card>


      </LeftRightLayout>

      <ProModal title='字典类型' ref={this.formRef}>
        <ProForm onFinish={this.handleSave} initialValues={this.state.formValues}>
          <ProFormItem name='id' noStyle/>
          <ProFormText label='名称' name='name' rules={[{required: true}]}/>
          <ProFormText label='编码' name='code' rules={[{required: true}]}/>
        </ProForm>
      </ProModal>


    </>
  }


}



