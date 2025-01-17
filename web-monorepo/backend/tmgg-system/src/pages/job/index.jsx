import {AutoComplete, Button, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Tag} from 'antd'
import React from 'react'
import {PlusOutlined} from "@ant-design/icons";
import {FieldComponent, HttpUtil, ProTable, SysUtil} from "@tmgg/tmgg-base";
import {DateUtil, StrUtil} from "@tmgg/tmgg-commons-lang";


const cronOptions = [
    {
        label: '*/5 * * * * ? 每隔5秒',
        value: '*/5 * * * * ?'
    },
    {
        label: '0 */5 * * * ? 每隔5分钟',
        value: '0 */5 * * * ?'
    },
    {
        label: '0 0 22 * * ? 每天22点',
        value: '0 0 22 * * ?'
    },
    {
        label: '0 0 1 * * ? 每天1点',
        value: '0 0 1 * * ?'
    },
    {
        label: '0 0 1 1 * ? 每月1号凌晨1点',
        value: '0 0 1 1 * ?'
    }
]


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,

        selectedRowKeys: [],

        jobClassOptions: [],

        paramList: []
    }

    componentDidMount() {
        HttpUtil.get('job/jobClassOptions').then(rs => {
            this.setState({jobClassOptions: rs})
        })
    }

    tableRef = React.createRef()
    formRef = React.createRef()

    columns = [

        {
            title: '名称',
            dataIndex: 'name',
        },
        {
            title: '执行类',
            dataIndex: 'jobClass',
        },
        {
            title: '分租',
            dataIndex: 'group',
        },
        {
            title: 'cron',
            dataIndex: 'cron',
        },



        {
            title: '参数',
            dataIndex: 'jobData',
            hideInSearch: true,
            render(list) {
                if (list)
                    return JSON.stringify(list)
            }
        },
        {
            title: '运行状态',
            dataIndex: 'executing',
            render: (v, record) => {
                return record.executing ? <Tag color='green'>运行中</Tag> : <Tag>空闲</Tag>
            },
        },
        {
            title: '触发时间',
            dataIndex: 'fireTime',
        },

        {
            title: '上次',
            dataIndex: 'previousFireTime',
        },
        {
            title: '下次',
            dataIndex: 'nextFireTime',
        },
        {
            title: '启用状态',
            dataIndex: 'enabled',
            render: (v, record) => {
                return record.enabled ? <Tag color='green'>启用</Tag> : <Tag color='red'>停用</Tag>
            },
        },

        {
            title: '操作',
            dataIndex: 'option',
            fixed: 'right',
            render: (_, record) => {
                let url = SysUtil.getServerUrl() + 'job/log/print?jobId=' + record.id;

                return (
                    <Space>
                        <a href={url} target='_blank'>日志</a>
                        <Button size='small' onClick={() => this.handleTriggerJob(record)}>执行一次</Button>
                        <Button size='small' onClick={() => this.handleEdit(record)}> 编辑 </Button>
                        <Popconfirm title='是否确定删除?' onConfirm={() => this.handleDelete(record)}>
                            <Button size='small'>删除</Button>
                        </Popconfirm>
                    </Space>
                );
            },
        },

    ]

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}, paramList: []})
    }
    handleEdit = (record) => {
        this.loadJobParamFields(record.jobClass, record.jobData)
        this.setState({formOpen: true, formValues: record,})
    }

    loadJobParamFields(className, jobData) {
        HttpUtil.post("job/getJobParamFields", jobData || {}, {className}).then(rs => {
            this.setState({paramList: rs})
        })
    }

    onFinish = (values) => {
        HttpUtil.post('job/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload();
        })
    }

    handleDelete = row => {
        const hide = message.loading("删除任务中...")
        HttpUtil.postForm('job/delete', {id: row.id}).then(rs => {
            this.tableRef.current.reload();
        }).catch(hide)
    }
    handleTriggerJob = row => {
        HttpUtil.get('job/triggerJob', {id: row.id}).then(rs => {
            this.tableRef.current.reload();
        })
    }


    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={(action, {selectedRowKeys}) => {
                    return [<Button type='primary' onClick={() => this.handleAdd()} icon={<PlusOutlined/>}>
                        新建
                    </Button>]
                }}
                request={(params, sort) => {
                    return HttpUtil.pageData('job/page', params, sort).then(rs => {
                        return rs;
                    });
                }}
                columns={this.columns}
                rowSelection={false}
                rowKey='id'

            />


            <Modal title='定时任务'
                   open={this.state.formOpen}
                   destroyOnClose
                   width={800}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onValuesChange={this.onValuesChange}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle>
                    </Form.Item>
                    <Form.Item label='执行类' name='jobClass' rules={[{required: true}]}
                               tooltip='org.quartz.Job接口，参考io.tmgg.job.builtin.DemoJob'>
                        <Select options={this.state.jobClassOptions}/>
                    </Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='cron表达式' name='cron' help='格式：秒分时日月周,留空表示手动执行'>
                        <AutoComplete placeholder='如 0 */5 * * * ?' options={cronOptions}/>
                    </Form.Item>

                    <Form.Item label='启用' name='enabled' valuePropName='checked' rules={[{required: true}]}>
                        <Switch/>
                    </Form.Item>

                    {this.state.paramList?.map(p => (
                        <Form.Item label={p.label}
                                   name={['jobData', p.name]}
                                   key={p.name}
                                   rules={[{required: p.required}]}>
                            <FieldComponent
                                type={p.componentType}
                                {...p.componentProps}
                            ></FieldComponent>
                        </Form.Item>
                    ))}


                </Form>

            </Modal>

        </>
    }

    onValuesChange = (changed, values) => {
        if (changed.jobClass) {
            this.loadJobParamFields(values.jobClass)
            const option = this.state.jobClassOptions.find(o => o.value === changed.jobClass)
            if (option) {
                let {label} = option;
                if (StrUtil.contains(label, " ")) { // 取中文名部门设置为name
                    this.formRef.current.setFieldValue("name", label.split(" ")[1])
                }
            }
        }

        if (changed.jobData) {
            this.loadJobParamFields(values.jobClass, values.jobData)
        }

    };
}



