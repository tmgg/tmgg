import {Button, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'
import {ButtonList, FieldDateRange, FieldUploadFile, HttpUtil, ProTable, SysUtil} from "@tmgg/tmgg-base";
import {CloudUploadOutlined} from "@ant-design/icons";


export default class extends React.Component {

    state = {
        formOpen: false,
        formValues: {}
    }

    tableRef = React.createRef()
    formRef = React.createRef()


    columns = [
        {
            title: '标识',
            dataIndex: 'id',
        },

        {
            title: '名称',
            tooltip: '上传时候的文件名',
            dataIndex: 'originName',
        },


        {
            title: '大小信息',
            dataIndex: 'fileSizeInfo',
        },

        {
            title: '对象名称',
            dataIndex: 'fileObjectName',
            tooltip: '文件唯一标识id'
        },

        {
            title: '文件类型（mime）',
            dataIndex: 'mimeType',
        },
        {
            title: '扩展名',
            dataIndex: 'fileSuffix',
        },

        {
            title: '上传时间',
            dataIndex: 'createTime',
        },
        {
            title: '上传者',
            dataIndex: 'createUserLabel',
        },

        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a href={SysUtil.wrapServerUrl( 'sysFile/preview/' + record.id) } target='_blank'>预览</a>
                    <Popconfirm perm='sysFile:delete' title='是否确定删除文件信息'
                                onConfirm={() => this.handleDelete(record)}>
                        <a>删除</a>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]


    handleDelete = row => {
        HttpUtil.postForm('sysFile/delete', row).then(rs => {
            this.tableRef.current.reload()
        })
    }



    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <Button type='primary' icon={<CloudUploadOutlined/>}
                                   onClick={() => this.setState({formOpen: true})}>
                        上传文件
                    </Button>
                }}
                request={(params) => {
                    return HttpUtil.pageData('sysFile/page', params);
                }}

                columns={this.columns}

                searchFormItemsRender={() => <>

                    <Form.Item label='文件名' name='originName'>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='对象名称' name='objectName'>
                        <Input/>
                    </Form.Item>


                    <Form.Item label='上传时间' name='dateRange'>
                        <FieldDateRange/>
                    </Form.Item>

                </>}
            />

            <Modal open={this.state.formOpen} title='上传文件'
                   width={800}
                   onCancel={() => {
                       this.setState({formOpen: false})
                       this.tableRef.current.reload()
                   }}
                   footer={null}
                   destroyOnHidden
            >
                <Form ref={this.formRef}
                      initialValues={this.state.formValues}
                     >
                    <Form.Item name='文件'>
                        <FieldUploadFile/>
                    </Form.Item>


                </Form>
            </Modal>

        </>
    }
}



