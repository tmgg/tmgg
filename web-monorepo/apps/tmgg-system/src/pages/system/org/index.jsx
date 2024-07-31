import {DeleteOutlined, EditOutlined, PlusOutlined, SyncOutlined} from '@ant-design/icons';
import {Button, Card, Col, Empty, Form, Input, InputNumber, message, Popconfirm, Row, Space, Switch, Tree} from 'antd';
import React from 'react';
import {FieldDictRadio, FieldRemoteTreeSelect, HttpClient} from "../../../common";

const baseTitle = "组织机构";
const baseApi = 'sysOrg/';

const deleteTitle = '删除' + baseTitle


const delApi = baseApi + 'delete'
const treeApi = baseApi + 'tree?showAll=true'


export default class extends React.Component {


  state = {

    formLoading: false,
    formValues: undefined,
    submitLoading: false,
    formEditing: false,


    showAll: true,
    treeData: [],
    treeLoading: false,
    defaultExpandedKeys: [],


    enableAllLoading:false,
  }
  actionRef = React.createRef();
  treeRef = React.createRef();

  componentDidMount() {
    this.loadTree(true)
  }

  loadTree = (showLoading) => {
    if(showLoading){
      this.setState({treeLoading: true})
    }

    const {showAll} = this.state
    HttpClient.get('sysOrg/tree', {showAll}).then(rs => {

      let treeData = rs.data;
      const defaultExpandedKeys = this.getAutoExpand(treeData)
      this.setState({treeData, defaultExpandedKeys})
    }).finally(() => {
      this.setState({treeLoading: false});
    })
  }

  // 如果只有一个根节点，则自动展开第一个
  getAutoExpand(treeData) {
    if (treeData && treeData.length === 1) {
      return [treeData[0].key]
    }
  }


  handleDelete = row => {
    HttpClient.post(delApi, row).then(rs => {
      this.setState({formValues: null})
      this.loadTree()
    })
  }


  onSelect = (selectedKeys) => {
    const key = selectedKeys[0]

    this.setState({formLoading: true, formEditing: false})
    HttpClient.get(baseApi + "detail", {id: key}).then(rs => {
      this.setState({formValues: rs.data})
    }).finally(() => {
      this.setState({formLoading: false})
    })
  }

  onFinish = (values) => {
    this.setState({submitLoading: true, formEditing: false})
    HttpClient.post(baseApi + 'save', values).then(rs => {
      message.success(rs.message)
      this.loadTree()
    }).finally(() => {
      this.setState({submitLoading: false})
    })
  }
  handleEnableAll =(id)=>{
    this.setState({enableAllLoading: true})
    HttpClient.get(baseApi + 'enableAll', {id}).then(rs => {
      message.success(rs.message)
      this.loadTree()
    }).finally(() => {
      this.setState({enableAllLoading: false})
    })
  }

  handleDisableAll =(id)=>{
    this.setState({enableAllLoading: true})
    HttpClient.get(baseApi + 'disableAll', {id}).then(rs => {
      message.success(rs.message)
      this.loadTree()
    }).finally(() => {
      this.setState({enableAllLoading: false})
    })
  }
  render() {
    let disabled = this.state.formValues == null;
    return <>
      <Row gutter={16} wrap={false}>
        <Col flex='400px'>
          <Card title='组织机构' loading={this.state.treeLoading}
                extra={<>
                  <Switch checked={this.state.showAll}
                          onChange={e => this.setState({showAll: e}, this.loadTree)}
                          checkedChildren='显示所有' unCheckedChildren="只显示启用"></Switch>
                  <Button icon={<SyncOutlined/>} type='link' onClick={this.loadTree}>刷新</Button>
                </>}>

            <Tree.DirectoryTree
              ref={this.treeRef}
              treeData={this.state.treeData}
              defaultExpandedKeys={this.state.defaultExpandedKeys}
              expandAction='doubleClick'

              onSelect={this.onSelect}
            >

            </Tree.DirectoryTree>


          </Card>
        </Col>
        <Col flex='auto'>
          <Card
            title='机构信息'
            loading={this.state.formLoading}
            extra={<Space>
              <Button onClick={() => {
                this.setState({
                  formLoading:true,
                  formEditing: true,
                  formValues:{
                    pid: this.state.formValues?.id,
                  }
                }, ()=>{
                  this.setState({formLoading:false})
                })
              }}>
                <PlusOutlined/> 新增
              </Button>
              <Button disabled={disabled} onClick={() => {
                this.setState({
                  formEditing: true
                })
              }}>
                <EditOutlined/> 编辑
              </Button>

              <Popconfirm title={'是否确定' + deleteTitle} disabled={disabled}
                          onConfirm={() => this.handleDelete(this.state.formValues)}>
                <Button icon={<DeleteOutlined/>}  disabled={disabled}>删除</Button>
              </Popconfirm>

              <Popconfirm
                title='启用本级及子节点'
                disabled={disabled}
                onConfirm={() => this.handleEnableAll(this.state.formValues?.id)}>
                <Button disabled={disabled}>启用本级及子节点</Button>
              </Popconfirm>

              <Popconfirm title='禁用本级及子节点' disabled={disabled}
                          onConfirm={() => this.handleDisableAll(this.state.formValues?.id)}>
                <Button  disabled={disabled}>禁用本级及子节点</Button>
              </Popconfirm>

            </Space>}
          >

            {this.state.formValues ?
              <Form
                disabled={!this.state.formEditing}
                labelCol={{flex: '150px'}}
                wrapperCol={{flex: '400px'}}
                initialValues={this.state.formValues}
                onFinish={this.onFinish}
              >
                <Form.Item noStyle name='id'>
                </Form.Item>
                <Form.Item label='父节点' name='pid'>
                  <FieldRemoteTreeSelect url={treeApi}/>
                </Form.Item>
                <Form.Item label='名称' name='name' rules={[{required: true}]}>
                  <Input></Input>
                </Form.Item>

                <Form.Item label='简称' name='shortName'>
                  <Input></Input>
                </Form.Item>

                <Form.Item label='类型' name='type' rules={[{required: true}]}>
                  <FieldDictRadio typeCode='org_type'/>
                </Form.Item>



                <Form.Item label='排序' name='seq'>
                  <InputNumber/>
                </Form.Item>

                <Form.Item label='状态' name='status' rules={[{required: true}]}>
                  <FieldDictRadio typeCode='common_status'/>
                </Form.Item>

                <Form.Item label='预留字段1' name='reservedField1'>
                  <Input></Input>
                </Form.Item>


                <Form.Item label='预留字段2' name='reservedField2'>
                  <Input></Input>
                </Form.Item>

                <Form.Item label='预留字段3' name='reservedField3'>
                  <Input></Input>
                </Form.Item>

                <Form.Item label=' ' colon={false}>
                  <Button type="primary" htmlType='submit' loading={this.state.submitLoading}>保存</Button>
                </Form.Item>

              </Form>
              :
              <Empty description='未选择机构'/>
            }


          </Card>

        </Col>
      </Row>


    </>
  }


}



