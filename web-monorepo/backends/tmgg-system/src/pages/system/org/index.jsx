import {DeleteOutlined, EditOutlined, PlusOutlined, SyncOutlined} from '@ant-design/icons';
import {Button, Card, Checkbox, Empty, Form, Input, InputNumber, message, Popconfirm, Space, Tree} from 'antd';
import React from 'react';
import {FieldDictRadio, FieldRemoteTreeSelect, HttpUtil, LeftRightLayout} from "@tmgg/tmgg-base";

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


        enableAllLoading: false,
    }
    actionRef = React.createRef();
    treeRef = React.createRef();

    componentDidMount() {
        this.loadTree(true)
    }

    loadTree = (showLoading) => {
        if (showLoading) {
            this.setState({treeLoading: true})
        }

        const {showAll} = this.state
        HttpUtil.get('sysOrg/tree', {showAll}).then(rs => {
            let treeData = rs;
            this.setState({treeData})
        }).finally(() => {
            this.setState({treeLoading: false});
        })
    }




    handleDelete = row => {
        HttpUtil.post(delApi, row).then(rs => {
            this.setState({formValues: null})
            this.loadTree()
        })
    }


    onSelect = (selectedKeys) => {
        const key = selectedKeys[0]

        this.setState({formLoading: true, formEditing: false})
        HttpUtil.get(baseApi + "detail", {id: key}).then(rs => {
            this.setState({formValues: rs})
        }).finally(() => {
            this.setState({formLoading: false})
        })
    }

    onFinish = (values) => {
        this.setState({submitLoading: true, formEditing: false})
        HttpUtil.post(baseApi + 'save', values).then(rs => {
            this.loadTree()
        }).finally(() => {
            this.setState({submitLoading: false})
        })
    }
    handleEnableAll = (id) => {
        this.setState({enableAllLoading: true})
        HttpUtil.get(baseApi + 'enableAll', {id}).then(rs => {
            this.loadTree()
        }).finally(() => {
            this.setState({enableAllLoading: false})
        })
    }

    handleDisableAll = (id) => {
        this.setState({enableAllLoading: true})
        HttpUtil.get(baseApi + 'disableAll', {id}).then(rs => {
            this.loadTree()
        }).finally(() => {
            this.setState({enableAllLoading: false})
        })
    }

    render() {
        let disabled = this.state.formValues == null;
        return <div>
            <LeftRightLayout leftSize={400}>
                <Card loading={this.state.treeLoading}
                      extra={<>
                          <Checkbox
                              checked={this.state.showAll}
                              onChange={e => {
                                  this.setState({showAll: e.target.checked}, this.loadTree);
                              }}
                          >包含禁用</Checkbox>
                          <Button size='small' icon={<SyncOutlined/>} onClick={this.loadTree}></Button>
                      </>}>

                    <Tree.DirectoryTree
                        ref={this.treeRef}
                        treeData={this.state.treeData}
                        defaultExpandedKeys={this.state.defaultExpandedKeys}
                        expandAction='doubleClick'

                        onSelect={this.onSelect}
                    >

                    </Tree.DirectoryTree>


                    {this.state.treeData.length === 0 && <Empty/>}
                </Card>
                <Card

                    loading={this.state.formLoading}
                    extra={<Space>
                        <Button type='primary' onClick={() => {
                            this.setState({
                                formLoading: true,
                                formEditing: true,
                                formValues: {
                                    pid: this.state.formValues?.id,
                                }
                            }, () => {
                                this.setState({formLoading: false})
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
                            <Button icon={<DeleteOutlined/>} disabled={disabled}>删除</Button>
                        </Popconfirm>

                        <Popconfirm
                            title='启用本级及子节点'
                            disabled={disabled}
                            onConfirm={() => this.handleEnableAll(this.state.formValues?.id)}>
                            <Button disabled={disabled}>启用本级及子节点</Button>
                        </Popconfirm>

                        <Popconfirm title='禁用本级及子节点' disabled={disabled}
                                    onConfirm={() => this.handleDisableAll(this.state.formValues?.id)}>
                            <Button disabled={disabled}>禁用本级及子节点</Button>
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


                            <Form.Item label='类型' name='type' rules={[{required: true}]}>
                                <FieldDictRadio typeCode='orgType'/>
                            </Form.Item>


                            <Form.Item label='排序' name='seq'>
                                <InputNumber/>
                            </Form.Item>

                            <Form.Item label='状态' name='status' rules={[{required: true}]}>
                                <FieldDictRadio typeCode='commonStatus'/>
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
                                <Button type="primary" htmlType='submit'
                                        loading={this.state.submitLoading}>保存</Button>
                            </Form.Item>

                        </Form>
                        :
                        <Empty description='未选择机构'/>
                    }


                </Card>
            </LeftRightLayout>


        </div>
    }


}


