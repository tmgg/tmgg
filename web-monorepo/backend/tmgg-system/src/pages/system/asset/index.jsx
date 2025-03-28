import {PlusOutlined} from '@ant-design/icons'
import {Button, Card, Form, Input, Modal, Popconfirm, Skeleton, Splitter, Tree, Typography} from 'antd'
import React from 'react'
import {
    ButtonList,
    dictValueTag,
    FieldDictRadio,
    FieldEditor,
    FieldRemoteSelect,
    FieldUploadFile,
    FieldUploadImage,
    HttpUtil,
    ProTable, SysUtil,
    ViewEllipsis,
    ViewImage
} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        treeData: [],
        treeLoading: true,
        selectedKey: null,

        formValues: {},
        formOpen: false,

        contentFormOpen: false,
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '名称',
            dataIndex: 'name',
        },

        {
            title: '编码',
            dataIndex: 'code',
        },
        {
            title: '类型',
            dataIndex: 'type',
            render(v) {
                return dictValueTag('sys_asset_type', v)
            },
        },
        {
            title: '文本内容',
            dataIndex: 'content',
            render: (v, record) => {
                return this.renderContent(record.type, v);
            }
        },

        {
            title: '尺寸',
            dataIndex: 'size',
        },


        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button disabled={record.type === 'DIR'} size='small' perm='sysAsset:save'
                            onClick={() => this.handleEditContent(record)}>内容</Button>
                    <Button disabled={record.code == null} size='small'
                           href={SysUtil.getFull('/sysAsset/preview/' + record.code)}>预览</Button>
                    <Button size='small' perm='sysAsset:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='sysAsset:delete' title='是否确定删除素材'
                                onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]


    componentDidMount() {
        this.loadTree();
    }

    loadTree() {
        this.setState({treeLoading: true})
        HttpUtil.get('sysAsset/tree').then(rs => {
            this.setState({treeData: rs})
        }).finally(() => this.setState({treeLoading: false}))
    }

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }
    handleEditContent = record => {
        this.setState({contentFormOpen: true, formValues: record})
    }
    onContentFinish = values => {
        HttpUtil.post('sysAsset/saveContent', values).then(rs => {
            this.setState({contentFormOpen: false})
            this.tableRef.current.reload()
        })
    }

    onFinish = values => {
        HttpUtil.post('sysAsset/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
            this.loadTree()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('sysAsset/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
            this.loadTree()
        })
    }


    render() {
        const {type} = this.state.formValues;

        return <>
            <Splitter>
                <Splitter.Panel defaultSize={250} style={{padding: 12}}>
                    <Typography.Text type='secondary'>素材分类</Typography.Text>
                    {this.state.treeLoading ? <Skeleton/> : <Tree
                        treeData={this.state.treeData}
                        defaultExpandAll
                        onSelect={(key) => this.setState({selectedKey: key[0]}, () => this.tableRef.current.reload())}
                        showIcon
                        blockNode
                    >
                    </Tree>}
                </Splitter.Panel>
                <Splitter.Panel style={{paddingLeft: 12}}>
                    <ProTable
                        actionRef={this.tableRef}
                        toolBarRender={() => {
                            return <ButtonList>
                                <Button perm='sysAsset:save' type='primary' onClick={this.handleAdd}>
                                    <PlusOutlined/> 新增
                                </Button>
                            </ButtonList>
                        }}
                        request={(params) => {
                            params.pid = this.state.selectedKey
                            return HttpUtil.pageData('sysAsset/page', params);
                        }}
                        columns={this.columns}
                    />
                </Splitter.Panel>
            </Splitter>


            <Modal title='素材'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
                   maskClosable={false}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onValuesChange={(changedValues, values) => this.setState({formValues: values})}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>
                    <Form.Item label='类型' name='type' rules={[{required: true}]}>
                        <FieldDictRadio typeCode="sys_asset_type"/>
                    </Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    {(type != null && type !== 'DIR') && <Form.Item label='编码' name='code' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>}

                    <Form.Item label='父文件夹' name='pid'>
                        <FieldRemoteSelect url={'sysAsset/dirOptions'}/>
                    </Form.Item>


                </Form>
            </Modal>

            <Modal title='素材内容'
                   open={this.state.contentFormOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({contentFormOpen: false})}
                   destroyOnClose
                   width={900}
                   maskClosable={false}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onContentFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input disabled/>
                    </Form.Item>

                    <Form.Item label='内容' name='content' rules={[{required: true}]}>
                        {this.renderContentFormItem(type)}
                    </Form.Item>
                </Form>
            </Modal>
        </>


    }

    renderContentFormItem(type) {
        if (type === 'TEXT') {
            return <FieldEditor/>
        }
        if (type === 'IMAGE') {
            return <FieldUploadImage maxNum={1} multiple={false}></FieldUploadImage>
        }
        if (type === 'VIDEO') {
            return <FieldUploadFile/>
        }
    }

    renderContent(type, value) {
        if (type === 'TEXT') {
            return <Typography.Text ellipsis={true}>{value}</Typography.Text>
        }
        if (type === 'IMAGE') {
            return <ViewImage value={value}></ViewImage>
        }
        if (type === 'VIDEO') {
            return <div>视频</div>
        }
    }
}


