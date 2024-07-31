import {Form, Modal, Tree} from 'antd';
import React from 'react';
import {FieldDictSelect, HttpClient} from "../../../common";


export default class UserOrgForm extends React.Component {


  state = {
    visible: false,
    treeData: [],
    checked: [],
    confirmLoading: false,

    formValues: {
      dataPermType: null
    },
  }


  handleSave = (values) => {

    values.grantOrgIdList = this.state.checked


    this.setState({
      confirmLoading: true
    })
    HttpClient.post('sysUser/grantData', values).then(rs => {
      this.setState({
        visible: false,
        confirmLoading: false
      })
    })


  }
  onCheck = (e) => {
    this.setState({
      checked: e.checked
    })
  };

  show(item) {
    console.log(item)
    this.setState({
      visible: true,
      formValues: item,
      checked: []
    })

    HttpClient.get('/sysUser/ownData', {id: item.id}).then(rs => {
      this.setState({checked: rs.data})
    })

    HttpClient.get('/sysOrg/tree').then(rs => {
      const list = rs.data;

      this.setState({treeData: list})
    })
  }

  formRef = React.createRef()


  render() {
    let {visible, treeData, confirmLoading, checked} = this.state

    return <Modal
      title="设置数据权限"
      destroyOnClose
      width={600}
      open={visible}
      confirmLoading={confirmLoading}
      onCancel={() => this.setState({visible: false})}
      onOk={() => this.formRef.current.submit()}
    >

      <Form ref={this.formRef}
            onFinish={this.handleSave}
            initialValues={this.state.formValues}
            onValuesChange={(change,values)=>{
                this.setState({formValues:values})
            }}
      >
        <Form.Item name='id' noStyle></Form.Item>
        <Form.Item label='类型' name='dataPermType' rules={[{required: true}]}>
          <FieldDictSelect typeCode='dataPermType'></FieldDictSelect>
        </Form.Item>
      </Form>


      {treeData.length > 0 && this.state.formValues.dataPermType === 'CUSTOM' && <Tree
        multiple
        checkable
        onCheck={this.onCheck}
        checkedKeys={checked}
        treeData={treeData}
        defaultExpandAll
        checkStrictly

      >
      </Tree>}


    </Modal>
  }


}



