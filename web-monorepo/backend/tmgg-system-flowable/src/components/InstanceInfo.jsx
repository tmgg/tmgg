import React from "react";
import {Card, Descriptions, Empty, Skeleton, Table, Tabs} from "antd";
import {Gap, HttpUtil, PageLoading} from "@tmgg/tmgg-base";
import PageRender from "@tmgg/tmgg-system/src/layouts/PageRender";


export default class InstanceInfo extends React.Component {

  state = {
    instanceCommentList: [],
    vars: {},

    id: null,
    starter: null,
    startTime: null,
    name: null,
    formUrl: null,

    data: {
      commentList: [],
      img: null
    },
    loading: true,

    errorMsg: null
  }


  componentDidMount() {
    const {id, businessKey} = this.props;

    HttpUtil.get("flowable/userside/getInstanceInfo", {id, businessKey}).then(rs => {
      this.setState(rs)
      this.setState({data: rs})

    }).catch(e => {
      this.setState({errorMsg: e})
    }).finally(()=>{
      this.setState({ loading: false})
    })

  }


  render() {
    if (this.state.errorMsg) {
      return <Empty description={this.state.errorMsg}></Empty>
    }

    const {data, loading} = this.state

    const {commentList, img, variables} = data
    if (loading) {
      return <Skeleton/>
    }

    console.log('流程ID', data.id)
    console.log('流程数据', data)

    return <div >
      <Card title='基本信息'>
        <Descriptions title={data.name}>
          <Descriptions.Item label='发起人'>{data.starter}</Descriptions.Item>
          <Descriptions.Item label='发起时间'>{data.startTime}</Descriptions.Item>

        </Descriptions>
        <Descriptions>
          <Descriptions.Item label='流程图'>     { img && <img  height={50} src={img} style={{maxWidth: '100%'}}/> }</Descriptions.Item>
        </Descriptions>
      </Card>

      <Gap />

      <Card title='表单'>

      {this.getForm()}
      </Card>
      <Gap />
      <Card title='处理记录' >
        <Table dataSource={commentList}
               bordered
               size='small'
               pagination={false} rowKey='id'
               columns={[
                 {
                   dataIndex: 'content',
                   title: '操作'
                 },
                 {
                   dataIndex: 'user',
                   title: '处理人'
                 },
                 {
                   dataIndex: 'time',
                   title: '处理时间'
                 },
               ]}
        />
      </Card>



    </div>

  }

  getForm = () => {
    const {data, loading} = this.state
    const {formUrl,formUrlSearch} = data

    if(!formUrl){
      return  <Empty description='表单获取失败'/>
    }
    console.log('表单路径',formUrl)
    return <PageRender pathname={formUrl} search={formUrlSearch}></PageRender>
  }
}
