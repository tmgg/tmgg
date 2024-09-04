import {AutoComplete, Button, Form, Input, message, Modal, Popconfirm, Select, Space, Switch, Table, Tag} from 'antd'
import React from 'react'
import {PlusOutlined} from "@ant-design/icons";
import { HttpUtil} from "@tmgg/tmgg-base";


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
    list: [],

    fileOptions:[],

    formValues: {},
    formOpen: false,

    selectedRowKeys: [],

    jobClassOptions: []
  }
  formRef = React.createRef()

  columns = [
    {
      title: '名称',
      dataIndex: 'name'
    },

    {
      title: '任务ID',
      dataIndex: 'description'
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


  componentDidMount() {
    this.loadData()

    HttpUtil.get('/kettle/file/options').then(rs=>{
      this.setState({fileOptions:rs})
    })
  }

  loadData() {
    HttpUtil.get('kettle/sysJob/list').then(rs => {
      this.setState({list: rs})
    })
  }

  handleAdd = () => {
    this.setState({formOpen: true, formValues: {}})
  }
  handleEdit = (record) => {
    this.setState({formOpen: true, formValues: record})
  }


  onFinish = (values) => {
    HttpUtil.post('kettle/sysJob/save', values).then(rs => {
      this.setState({formOpen: false})
      this.loadData()
    })
  }

  handleDelete = row => {
    const hide = message.loading("删除任务中...")
    HttpUtil.get('kettle/sysJob/delete', {id: row.id}).then(rs => {
      this.loadData()
    }).catch(hide)
  }

  handleTriggerJob = row => {
    HttpUtil.get('kettle/sysJob/triggerJob', {id: row.id}).then(rs => {
      this.loadData()
    })
  }

  onValuesChange = (changed, values) => {
    console.log('修改数据', changed)
    let jobId = changed.description;
    if (jobId) {
      const option = this.state.fileOptions.find(o => o.value === jobId)
      if (option) {
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
  render() {
    return <>   <div style={{display: 'flex', justifyContent: 'end'}}>
      <Button type='primary' onClick={() => this.handleAdd()} icon={<PlusOutlined/>}>
        新建
      </Button>
    </div>

      <Table
        columns={this.columns}
        dataSource={this.state.list}
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
            <Input />
          </Form.Item>

          <Form.Item label='任务' name='description' rules={[{required: true}]}>
            <Select options={this.state.fileOptions}></Select>
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

                  <Form.Item label='参数名' name={[name, 'key']} {...restField}>
                    <Input/>
                  </Form.Item>
                  <Form.Item label='参数值' name={[name, 'value']} {...restField}>
                    <Input/>
                  </Form.Item>

                </Space>
              )}


            </>
            }
          </Form.List>
        </Form>

      </Modal>

    </>
  }


}



