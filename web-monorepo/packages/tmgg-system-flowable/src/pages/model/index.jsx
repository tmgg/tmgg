import {Button, Form, Input, message, Modal, Popconfirm, Select, Space} from 'antd';
import React from 'react';
import {ProTable} from "@ant-design/pro-components";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {http} from "@tmgg/tmgg-base";

const baseTitle = "流程模型";
const baseApi = 'flowable/model/';
const basePerm = 'flowable/model:';

const deleteTitle = '删除' + baseTitle


const delApi = baseApi + 'delete'
const pageApi = baseApi + 'page'

const delPerm = basePerm + 'delete'


export default class extends React.Component {


  state = {
    formValues: {},
    formOpen:false
  }

  actionRef = React.createRef();
  formRef = React.createRef();


  columns = [
    {
      title: '模型名称',
      dataIndex: 'name',
      sorter: true
    },
    {
      title: '唯一编码',
      dataIndex: 'code'
    },
    {
      title: '表单链接',
      dataIndex: 'formLink'
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      hideInForm: true,
      hideInSearch: true
    },


    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Space>
          <a href={'design.html?id=' + record.id}> 设计 </a>
          <a href={'test.html?id=' + record.id}> 测试 </a>
          <a onClick={()=>this.handleEdit(record)}> 修改 </a>
          <Popconfirm perm={delPerm} title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>
        </Space>
      ),
    },
  ];


  handleAdd = () => {
    this.setState({
      formOpen:true,
      formValues:{}
    })
  }

  handleEdit = record=>{
    this.setState({
      formOpen:true,
      formValues:record
    })
  }
  onFinish = values=>{
    http.post('flowable/model/save',values).then(rs=>{
      message.success(rs.message)
      this.actionRef.current.reload()
      this.setState({formOpen:false})
    })
  }

  handleDelete = row => {
    http.get(delApi, {id:row.id}).then(rs => {
      rs.success ? message.success(rs.message) : message.error(rs.message)
      this.actionRef.current.reload();
    })
  }


  render() {
    return <>
      <ProTable
        search={false}
        actionRef={this.actionRef}
        toolBarRender={() => <Button icon={<PlusOutlined/>} type='primary' onClick={this.handleAdd}>新建</Button>}
        request={(params, sort) => http.getPageable(pageApi, params, sort)}
        columns={this.columns}
        rowSelection={false}
        rowKey="id"
        options={{search:true}}
      />

      <Modal title='模型基本信息'
             open={this.state.formOpen}
             destroyOnClose
             onOk={() => this.formRef.current.submit()}
             onCancel={() => this.setState({formOpen: false})}
             width={700}
      >

        <Form ref={this.formRef} labelCol={{flex: '100px'}}
              initialValues={this.state.formValues}
              onFinish={this.onFinish}>
          <Form.Item name='id' noStyle>
          </Form.Item>
          <Form.Item label='名称' name='name' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='编码' name='code' rules={[{required: true}]} help='流程定义的key，全局唯一， 英文'>
            <Input/>
          </Form.Item>

          <Form.Item label='表单地址' name='formLink' rules={[{required: true}]} help={"支持变量， 如 /user/form?id=${businessKey}"}>
            <Input/>
          </Form.Item>


          <Form.List label='变量定义' name='conditionVariableList'>
            {(fields, {add, remove}, {errors}) => <>

              {fields.map(({key, name, ...restField}, index) => <Space
                  key={key}
                  style={{
                    display: 'flex',
                    marginBottom: 8,
                  }}
                  align="baseline"
                >
                  <Form.Item label='参数' name={[name, 'name']} {...restField} >
                    <Input />
                  </Form.Item>
                  <Form.Item label='显示' name={[name, 'label']} {...restField} >
                    <Input/>
                  </Form.Item>
                <Form.Item label='类型' name={[name, 'valueType']} {...restField} >
                  <Select style={{width: 100}} options={[{label:'文本',value:'text'},{label:'数字', value: 'digit'}]}/>
                </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)}/>

                </Space>
              )}

              <Form.Item label=' ' colon={false}>
                <Button
                  icon={<PlusOutlined/>}
                  type="dashed"
                  onClick={() => add()}
                  style={{
                    width: '60%',
                  }}
                >
                  添加参数
                </Button>
              </Form.Item>
            </>
            }
          </Form.List>
        </Form>

      </Modal>
    </>
  }


}



