import {Button, Checkbox, Empty, Form, Modal, Radio, Space, Tabs} from 'antd'
import React from 'react'
import {FieldText,  HttpUtil, ProTable} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,

        id: null,

        templateOptions: [],
        genItemsDetails: [],
        genFiles:[],

        tabItems:[]
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
        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => {
                return <Space>
                    <Button size={"small"} onClick={() => this.handleGen(record.id)}>生成</Button>
                </Space>
            }
        }

    ]

    componentDidMount() {
        HttpUtil.get('code/gen/templateOptions').then(templateOptions => {
            this.setState({templateOptions})
        })
    }

    handleGen = (id) => {
        this.setState({formOpen: true, id, genItemsDetails: [],tabItems:[]})
    }

    onValuesChange = (changeValues, values) => {
        if (changeValues.template) {
            HttpUtil.get('code/gen/genDetail', {id: values.id, template: values.template}).then(genItemsDetails => {
                for (let item of genItemsDetails) {
                    item.children = <pre style={{height: 300, overflowY: 'scroll',fontSize:'xx-small'}} contentEditable>{item.children}</pre>
                }
                this.setState({genItemsDetails, tabItems: genItemsDetails})
                this.formRef.current.setFieldsValue({
                    files: genItemsDetails.map(item=>item.key)
                })
            })
        }
        if(changeValues.files){
            const tabItems = []
            for(let key  of changeValues.files){
                let item = this.state.genItemsDetails.find(i=>i.key == key)
                tabItems.push(item)
            }
            this.setState({tabItems})
        }
    }


    onFinish = (values) => {
        HttpUtil.post('code/gen', values).then(rs => {
            //this.setState({formOpen: false})
        }).catch(err => {
            alert(JSON.stringify(err))
        })
    }




    render() {
        return <>

            <ProTable
                actionRef={this.tableRef}
                request={(params, sort) => {
                    return HttpUtil.pageData('code/entity/page', params, sort)
                }}
                columns={this.columns}
                rowKey='id'
                search={false}
                options={{
                    search: true,
                }}

            />


            <Modal title='代码生成' open={this.state.formOpen} onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden={true}
                   width={'90vw'}
                   maskClosable={false}
                   okText='生成到项目中'

            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      onValuesChange={this.onValuesChange}
                      onFinish={this.onFinish}>
                    <Form.Item label='实体类' name='id' rules={[{required: true}]} initialValue={this.state.id}>
                        <FieldText/>
                    </Form.Item>
                    <Form.Item label='模板' name='template' rules={[{required: true}]} >
                        <Radio.Group options={this.state.templateOptions} />
                    </Form.Item>
                    <Form.Item label='选择文件' name='files' rules={[{required: true}]}>
                        <Checkbox.Group options={this.state.genItemsDetails}></Checkbox.Group>
                    </Form.Item>

                    <div>
                        {this.state.tabItems.length == 0 && <Empty description='请选择文件' />}
                        <Tabs items={this.state.tabItems} type='card'></Tabs>
                    </div>

                </Form>


            </Modal>

        </>
    }
}



