import {PlusOutlined} from '@ant-design/icons'
import {Button, Popconfirm, Modal, Form, Input, Tag} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/tmgg-base'
import {ButtonList, FieldComponent, FieldText, HttpUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [
        {
            title: '名称',
            dataIndex: 'label',
        },
        {
            title: '键',
            dataIndex: 'id',
        },

        {
            title: '值',
            dataIndex: 'value',
            render(v,record){
                if(v != null){
                    return <FieldComponent type={record.valueType || 'input'} mode='read' value={v} />
                }
                let defaultValue = record.defaultValue;
                if(defaultValue != null){
                    return <div>
                        <FieldComponent type={record.valueType || 'input'} mode='read' value={defaultValue}/>
                        <Tag color={"orange"} style={{marginLeft:8}}>默认值</Tag></div>

                }
            }
        },



        {
            title: '备注',
            dataIndex: 'remark',
            width: 400
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
        },

        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='sysConfig:save' onClick={() => this.handleEdit(record)}> 编辑 </Button>
                </ButtonList>
            ),
        },
    ]


    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('sysConfig/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                request={(params, sort) => HttpUtil.pageData('sysConfig/page', params, sort)}
                columns={this.columns}
                search={false}
                defaultPageSize={20}
            />

            <Modal      title={'编辑系统参数'}
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
                   maskClosable={false}
                   width={400}
            >

                <Form ref={this.formRef}
                      style={{marginTop:24}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                      layout='vertical'
                >

                    <Form.Item name='id' noStyle/>

                    <Form.Item  name='value' label={this.state.formValues.label}>
                        <FieldComponent type={this.state.formValues.valueType || 'input'} />
                    </Form.Item>

                </Form>
            </Modal>
        </>


    }
}



