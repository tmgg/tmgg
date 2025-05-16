import React from "react";
import {Card, Descriptions, Empty, Skeleton, Table, Tabs} from "antd";
import {HttpUtil, PageLoading} from "@tmgg/tmgg-base";
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
      this.setState({data: rs, loading: false})

    }).catch(e => {
      this.setState({errorMsg: e})
    })

  }


  render() {
    if (this.state.errorMsg) {
      return <Empty description={this.state.errorMsg}></Empty>
    }

    const {data, loading} = this.state
    const {formUrl} = data

    const {commentList, img, variables} = data
    if (loading) {
      return <Skeleton/>
    }

    console.log('流程ID', data.id)

    return <div >
      <Card>
        <Descriptions title={data.name}>
          <Descriptions.Item label='发起人'>{data.starter}</Descriptions.Item>
          <Descriptions.Item label='发起时间'>{data.startTime}</Descriptions.Item>
          {Object.keys(variables).map(key => <Descriptions.Item key={key}
                                                                label={key}>{variables[key]}</Descriptions.Item>)}
        </Descriptions>
      </Card>
      <Card style={{marginTop: 12}}>
        <Tabs items={[
          {
            key: '1',
            label: '表单',
            children: this.getForm(formUrl)
          },
          {
            key: '2', label: '审核记录',
            children: <Table dataSource={commentList}
                             bordered
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
          },
          {
            key: '3', label: '流程图',
            children: img ? <img src={img} style={{maxWidth: '100%'}}/> : <PageLoading/>
          },
        ]}></Tabs>
      </Card>

    </div>

  }

  getForm = formUrl => {
    if(!formUrl){
      return  <Empty description='表单获取失败'/>
    }
    return <PageRender pathname={formUrl}></PageRender>
  }
}
