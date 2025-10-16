import React from "react";
import {Card, Empty, Modal, Skeleton, Table, Tabs, Typography} from "antd";
import {Gap, HttpUtil} from "@tmgg/tmgg-base";
import {PageRender} from "@tmgg/tmgg-system";
import {FormOutlined, ShareAltOutlined} from "@ant-design/icons";


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
        HttpUtil.get("flowable/userClient/getInstanceInfo", {id, businessKey}).then(rs => {
            this.setState(rs)
            this.setState({data: rs})
        }).catch(e => {
            this.setState({errorMsg: e})
        }).finally(() => {
            this.setState({loading: false})
        })

    }

    onImgClick = () => {
        const {data} = this.state

        const {img} = data
        Modal.info({
            title: '流程图',
            width: '70vw',
            content: <div style={{width: '100%', overflow: 'auto', maxHeight: '80vh'}}><img src={img}/></div>
        })
    };

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

        return <>
            <Typography.Title level={4}>{data.name}</Typography.Title>
            <Typography.Text type="secondary">{data.starter} &nbsp;&nbsp; {data.startTime}</Typography.Text>
            <Gap></Gap>
            <Tabs
                type="card"
                items={[
                    {
                        key: '1',
                        label: '表单',
                        icon: <FormOutlined/>,
                        children: this.renderForm()
                    },
                    {
                        key: '2',
                        label: '流程',
                        icon: <ShareAltOutlined/>,
                        children: this.renderProcess(img, commentList)
                    }
                ]}>

            </Tabs>


        </>

    }

    renderProcess = (img, commentList) => <Card title='处理记录'>
        {img && <img src={img} style={{maxWidth: '100%'}}
                     onClick={this.onImgClick}/>}
        <Table dataSource={commentList}
               bordered
               size='small'
               pagination={false}
               rowKey='id'
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
    </Card>;

    renderForm = () => {
        const {data} = this.state
        const {processDefinitionKey, businessKey} = data

        let pathname = '/flowable/form/' + processDefinitionKey;
        console.log('流程表单路径为:', pathname)
        return <PageRender pathname={pathname} search={'?id=' + businessKey + '&formKey=' + this.props.formKey}
                           passLocation={true}/>
    }
}
