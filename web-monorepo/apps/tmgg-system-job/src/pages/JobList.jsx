import {AutoComplete, Button, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Tag} from 'antd'
import React from 'react'
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import StreamLog from "../components/StreamLog";
import {ProTable} from "@ant-design/pro-table";
import {http} from "@tmgg/tmgg-base";


const cronOptions = [
  {
    label: '0 */1 * * * ? 每隔1分钟执行一次',
    value: '0 */1 * * * ?'
  },
  {
    label: '0 0 22 * * ? 每天22点执行一次',
    value: '0 0 22 * * ?'
  },
  {
    label: '0 0 1 1 * ? 每月1号凌晨1点执行一次',
    value: '0 0 1 1 * ?'
  }
]


export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false,

    selectedRowKeys: [],

    jobClassOptions: []
  }

  componentDidMount() {
    http.get('job/jobClassOptions').then(rs => {
      this.setState({jobClassOptions: rs.data})
    })
  }

  tableRef = React.createRef()
  formRef = React.createRef()

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
      render(_, record) {
        return <>
          <div>{record.name}</div>
          <div>{record.jobClass}</div>
        </>
      }
    },


    {
      title: 'cron',
      dataIndex: 'cron',
      hideInSearch: true,
      render(v, record) {
        return <>
          <div>{v}</div>
          <div>上次触发：{record.previousFireTime}</div>
          <div>下次触发：{record.nextFireTime}</div>
        </>;
      }
    },
    {
      title: '参数',
      dataIndex: 'jobData',
      hideInSearch: true,
      render(list) {
        if (list) {
          const str = [];
          for (let item of list) {
            str.push(item.key + "=" + item.value)
          }
          return str.join('&')
        }
      }
    },
    {
      title: '启用状态',
      dataIndex: 'enabled',
      render: (v, record) => {
        return record.enabled ? <Tag color='green'>启用</Tag> : <Tag color='red'>停用</Tag>
      },
      valueEnum: {
        'true': '是',
        'false': '否'
      },
    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return (
          <Space>
            <Button size='small' onClick={() => {
              Modal.info({
                title: '任务日志',
                icon: null,
                width: 1024,
                closable: true,
                content: <StreamLog url={'/job/log/' + record.id}/>
              })
            }}>日志</Button>
            <Button size='small' onClick={() => this.handleTriggerJob(record)}>执行一次</Button>
            <Button size='small' onClick={() => this.handleEdit(record)}> 修改 </Button>
            <Popconfirm title='是否确定删除?' onConfirm={() => this.handleDelete(record)}>
              <Button size='small'>删除</Button>
            </Popconfirm>
          </Space>
        );
      },
    },

  ]

  handleAdd = () => {
    this.setState({formOpen: true, formValues: {}})
  }
  handleEdit = (record) => {
    this.setState({formOpen: true, formValues: record})
  }


  onFinish = (values) => {
    http.post('job/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload();
      message.success(rs.message)
    }).catch(err => {
      alert(err)
    })
  }

  handleDelete = row => {
    const hide = message.loading("删除任务中...")
    http.get('job/delete', {id: row.id}).then(rs => {
      message.success(rs.message)
      this.tableRef.current.reload();
    }).catch(hide)
  }
  handleTriggerJob = row => {
     http.get('job/triggerJob', {id: row.id}).then(rs => {
      message.success(rs.message)
      this.tableRef.current.reload();
    })
  }


  render() {
    return <>
      <ProTable
        actionRef={this.tableRef}
        toolBarRender={(action, {selectedRowKeys}) => {
          return <Button type='primary' onClick={() => this.handleAdd()} icon={<PlusOutlined/>}>
            新建
          </Button>
        }}
        request={(params, sort) => {
          return http.requestPageData('job/page', params, sort).then(rs => {
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
             onOk={() => this.formRef.current.submit()}
             onCancel={() => this.setState({formOpen: false})}
      >

        <Form ref={this.formRef} labelCol={{flex: '100px'}}
              initialValues={this.state.formValues}
              onValuesChange={this.onValuesChange}
              onFinish={this.onFinish}>
          <Form.Item name='id' noStyle>
          </Form.Item>
          <Form.Item label='名称' name='name' rules={[{required: true}]}>
            <Input/>
          </Form.Item>
          <Form.Item label='执行类' name='jobClass' rules={[{required: true}]} help='如 cn.crec.job.DemoJob'>
            <Select options={this.state.jobClassOptions}/>
          </Form.Item>
          <Form.Item label='cron表达式' name='cron' rules={[{required: true}]} help='秒 分 时 日 月 周'>
            <AutoComplete placeholder='如 0 */1 * * * ?' options={cronOptions}/>
          </Form.Item>
          <Form.Item label='启用' name='enabled' valuePropName='checked' rules={[{required: true}]}>
            <Switch/>
          </Form.Item>
          <Form.List label='参数' name='jobData'>
            {(fields, {add, remove}, {errors}) => <>

              {fields.map(({key, name, ...restField}, index) => <Space
                  key={key}
                  style={{
                    display: 'flex',
                    marginBottom: 8,
                  }}
                  align="baseline"
                >
                  <Form.Item label='描述' name={[name, 'label']} {...restField}>
                    <Input/>
                  </Form.Item>
                  <Form.Item label='参数名' name={[name, 'key']} {...restField}>
                    <Input/>
                  </Form.Item>
                  <Form.Item label='参数值' name={[name, 'value']} {...restField}>
                    <Input/>
                  </Form.Item>

                  <MinusCircleOutlined onClick={() => remove(name)}/>

                </Space>
              )}

              <Form.Item label=' ' colon={false}>
                <Button
                  icon={<PlusOutlined/>}
                  type="dashed"
                  onClick={() => add()}
                  style={{
                    width: '60%',
                  }}
                >
                  添加参数
                </Button>
              </Form.Item>
            </>
            }
          </Form.List>
        </Form>

      </Modal>

    </>
  }

  onValuesChange = (changed, values) => {
    console.log('修改数据', changed)
    if (changed.jobClass) {
      const option = this.state.jobClassOptions.find(o => o.value === changed.jobClass)
      if (option) {
        this.formRef.current.setFieldValue("name", option.label)

        const fields = option.data;
        if(fields){
          let {jobData} = values;
          if (jobData == null || jobData.length === 0) {
            jobData = option.data
            this.formRef.current.setFieldValue("jobData", jobData)
          }
        }


      }
    }
  };
}



