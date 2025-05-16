import {PlusOutlined, UploadOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm, Splitter, Tree, Typography} from 'antd'
import React from 'react'
import {
    ButtonList,
    dictValueTag,
    FieldEditor,
     FieldSelect,
    FieldUploadFile,
    FieldUploadImage,
    HttpUtil,
    Page,
    ProTable,
    SysUtil,
    ViewImage
} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        selectedKey: null,

        formValues: {},
        formOpen: false,

        contentFormOpen: false,
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [
        {
            title: '文件夹',
            dataIndex: ['dir','name'],
            sorter:true
        },
        {
            title: '类型',
            dataIndex: 'type',
            render(v) {
                return dictValueTag('assetType', v)
            },
        },
        {
            title: '名称',
            dataIndex: 'name',
            sorter:true
        },

        {
            title: '内容',
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
                <ButtonList maxNum={3}>
                    <Button size='small' type='primary'
                            perm='sysAsset:save'
                            onClick={() => this.handleEditContent(record)}>上传</Button>

                    <Button size='small'
                            href={SysUtil.wrapServerUrl('/sysAsset/preview/' + record.name)}
                            target='_blank'>预览</Button>

                    <Button size='small' perm='sysAsset:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='sysAsset:delete' title='是否确定删除素材'
                                onConfirm={() => this.handleDelete(record)}>
                        <a>删除</a>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]


    handleAdd = (type) => {
        this.setState({formOpen: true, formValues: {type}})
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
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('sysAsset/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }


    render() {
        const {type} = this.state.formValues;

        return <Page>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='sysAsset:save' type='primary' onClick={()=>this.handleAdd(1)}>
                           <UploadOutlined /> 上传文件
                        </Button>
                        <Button perm='sysAsset:save' type='primary' onClick={()=>this.handleAdd(0)}>
                            <PlusOutlined/> 新增富文本
                        </Button>
                    </ButtonList>
                }}
                request={(params) => {
                    params.pid = this.state.selectedKey
                    return HttpUtil.pageData('sysAsset/page', params);
                }}
                columns={this.columns}
            />


            <Modal title='素材'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden
                   maskClosable={false}
                   width={900}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onValuesChange={(changedValues, values) => this.setState({formValues: values})}
                      onFinish={this.onFinish}
                >

                    <Form.Item name='id' noStyle />
                    <Form.Item  name='type' noStyle />

                    <Form.Item label='内容' name='content'>
                        {this.renderContentFormItem(type)}
                    </Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input />
                    </Form.Item>
                    <Form.Item label='所属文件夹' name={['dir','id']} rules={[{required: true}]}>
                        <FieldSelect url={'sysAssetDir/options'}/>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal title='素材内容'
                   open={this.state.contentFormOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({contentFormOpen: false})}
                   destroyOnHidden
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
        </Page>


    }

    renderContentFormItem = type => {
        if (type === 0) {
            return <FieldEditor/>
        }

        return <FieldUploadFile maxCount={1} onFileChange={fileList => {
            this.formRef.current.setFieldsValue({name:fileList[0]?.name})
        }}/>
    };

    renderContent(type, value) {
        if (value == null) {
            return
        }
        if (type ===0) {
            let el = document.createElement('div');
            el.innerHTML = value
            return <Typography.Text ellipsis={true}>{el.innerText}</Typography.Text>
        }


        return <ViewImage value={value} />
    }
}


