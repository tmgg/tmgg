import {Form, Input, Popconfirm} from 'antd'
import React from 'react'
import {ButtonList, FieldDateRange, HttpUtil, ProTable} from "@tmgg/tmgg-base";


export default class extends React.Component {


    tableRef = React.createRef()

    columns = [
        {
            title: 'id',
            dataIndex: 'id',
        },
        {
            title: '存储位置',
            dataIndex: 'storageType',
        },

        {
            title: '名称',
            tooltip: '上传时候的文件名',
            dataIndex: 'fileOriginName',
        },

        {
            title: '后缀',
            dataIndex: 'fileSuffix',
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
                request={(params) => {
                    return HttpUtil.pageData('sysFile/page', params);
                }}
                columns={this.columns}
                searchFormItemsRender={() => <>

                    <Form.Item label='文件名' name='fileOriginName'>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='对象名称' name='fileObjectName'>
                        <Input/>
                    </Form.Item>


            
                    <Form.Item label='上传时间' name='dateRange'>
                        <FieldDateRange/>
                    </Form.Item>

                </>}
            />
        </>
    }
}



