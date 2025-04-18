import {DeleteOutlined, EditOutlined, PlusOutlined, SyncOutlined} from '@ant-design/icons';
import {Button, Card, Checkbox, Empty, Form, Input, Popconfirm, Space, Splitter, Switch, Tree} from 'antd';
import React from 'react';
import {FieldDictRadio, FieldRadioBoolean, FieldRemoteTreeSelect, HttpUtil, NamedIcon, Page} from "@tmgg/tmgg-base";

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
        draggable:false,


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
        HttpUtil.get('sysOrg/pageTree', {showAll}).then(rs => {
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
        if (selectedKeys.length === 0) {
            this.setState({formValues: null})
            return
        }

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

    onDraggableChange = e => {
        this.setState({draggable:e})
    };

    render() {
        let {formValues} = this.state;
        let disabled = formValues == null;
        return <Page>
            <Splitter>
                <Splitter.Panel defaultSize={400}>
                    <Card loading={this.state.treeLoading}
                          title='组织机构'
                          extra={<Space>
                              <div>
                              排序&nbsp;<Switch
                              value={this.state.draggable}
                              onChange={this.onDraggableChange}/> </div>
                              <div>
                              包含禁用&nbsp;<Switch
                                  value={this.state.showAll}
                                  onChange={e => {
                                      this.setState({showAll: e}, this.loadTree);
                                  }}
                              />
                              </div>
                              <Button size='small' icon={<SyncOutlined/>} onClick={this.loadTree}></Button>
                          </Space>}>

                        {this.state.treeLoading || <Tree
                            ref={this.treeRef}
                            treeData={this.state.treeData}
                            onSelect={this.onSelect}
                            defaultExpandAll
                            showIcon
                            blockNode
                            icon={item => {
                                return <NamedIcon name={item.data.iconName}/>
                            }}
                            draggable={this.state.draggable}
                            onDrop={this.onDrop}
                            showLine

                        >
                        </Tree>}
                        {this.state.treeData.length === 0 && <Empty/>}
                    </Card>
                </Splitter.Panel>

                <Splitter>
                    <Card
                        loading={this.state.formLoading}
                        extra={<Space>
                            <Button type='primary' onClick={() => {
                                this.setState({
                                    formLoading: true,
                                    formEditing: true,
                                    formValues: {
                                        pid: formValues?.id,
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
                                        onConfirm={() => this.handleDelete(formValues)}>
                                <Button icon={<DeleteOutlined/>} disabled={disabled}>删除</Button>
                            </Popconfirm>

                            <Popconfirm
                                title='启用本级及子节点'
                                disabled={disabled}
                                onConfirm={() => this.handleEnableAll(formValues?.id)}>
                                <Button disabled={disabled}>启用本级及子节点</Button>
                            </Popconfirm>

                            <Popconfirm title='禁用本级及子节点' disabled={disabled}
                                        onConfirm={() => this.handleDisableAll(formValues?.id)}>
                                <Button disabled={disabled}>禁用本级及子节点</Button>
                            </Popconfirm>

                        </Space>}
                    >

                        {formValues == null ? <Empty description='未选择机构'/> : <Form
                            disabled={!this.state.formEditing}
                            labelCol={{flex: '150px'}}
                            wrapperCol={{flex: '400px'}}
                            initialValues={formValues}
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


                            <Form.Item label='状态' name='enabled' rules={[{required: true}]}>
                                <FieldRadioBoolean/>
                            </Form.Item>


                            <Form.Item label=' ' colon={false}>
                                <Button type="primary" htmlType='submit'
                                        loading={this.state.submitLoading}>保存</Button>
                            </Form.Item>

                        </Form>
                        }


                    </Card>
                </Splitter>

            </Splitter>


        </Page>
    }

    onDrop = ({dragNode, dropPosition, dropToGap, node}) => {
        const dropKey = node.key;
        const dragKey = dragNode.key;
        console.log(dragNode.title, '->', node.title, 'dropToGap', dropToGap, dropPosition)
        HttpUtil.post('/sysOrg/sort', {dropPosition, dropToGap, dropKey, dragKey}).then(this.loadTree)
    };
}



