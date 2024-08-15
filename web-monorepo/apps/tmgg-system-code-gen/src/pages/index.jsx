import {Button, Form, message, Modal, Radio} from 'antd'
import React from 'react'
import {ProTable} from '@ant-design/pro-components'
import HttpClient from "../HttpClient";


export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,

    selectedRowKeys: []
  }

  tableRef = React.createRef()
  formRef = React.createRef()

  columns = [
    {
      title: '简称',
      dataIndex: 'simpleName',
    },
    {
      title: '备注（@Remark注解)',
      dataIndex: 'remark',
    },

    {
      title: '全称',
      dataIndex: 'name',
    },
    {
      title: '父类',
      dataIndex: 'superclassSimpleName',
    },


  ]

  handleGen = (selectedRowKeys) => {
    this.setState({formOpen: true, selectedRowKeys})
  }


  onFinish = (values) => {
    values.ids = this.state.selectedRowKeys
    HttpClient.post('code/gen', values).then(rs => {
      this.setState({formOpen: false})
      message.success(rs.message)
    }).catch(err => {
      alert(JSON.stringify(err))
    })
  }


  render() {
    return <>
      <ProTable
        actionRef={this.tableRef}
        toolBarRender={(action, {selectedRowKeys}) => {
          return <>
            <Button type='primary' onClick={() => this.handleGen(selectedRowKeys)}>
              生成代码
            </Button>
          </>
        }}
        request={(params, sort) => {
          return HttpClient.getPageableData('code/entity/page', params, sort).catch(err => alert("错误" + err));
        }}
        columns={this.columns}
        rowSelection={{}}
        rowKey='id'
        search={false}

      />


      <Modal title='代码生成' open={this.state.formOpen} onOk={() => this.formRef.current.submit()}
             onCancel={() => this.setState({formOpen: false})}
              destroyOnClose={true}
      >

        <Form ref={this.formRef} labelCol={{flex: '100px'}} onFinish={this.onFinish}>
          <Form.Item label='模板' name='template' rules={[{required: true}]} initialValue='list'>
            <Radio.Group>
              <Radio value='list'>列表</Radio>
              <Radio value='tree' >树(左数右表单)</Radio>
              <Radio value='tree-list' disabled>左树右表格(只选两个)</Radio>
            </Radio.Group>
          </Form.Item>


          <Form.Item label='生成方式' name='genType' rules={[{required: true}]} initialValue='project'>
            <Radio.Group>
              <Radio value='project'>生成到项目中</Radio>
              <Radio value='zip' disabled>下载压缩包</Radio>
            </Radio.Group>
          </Form.Item>


        </Form>


      </Modal>

    </>
  }
}



