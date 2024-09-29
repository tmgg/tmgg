import {Card, Col, Row, Descriptions, Space} from 'antd';
import React, {Fragment} from 'react';


import {HttpUtil} from "@tmgg/tmgg-base";

const {Item} = Descriptions


export default class extends React.Component {

    state = {
        loading: true,
        info: {}
    }

    componentDidMount() {
        this.setState({loading: true})
        HttpUtil.get('sysDatasource/status').then(rs => {
            this.setState({loading: false})
            this.setState({info: rs})
        })
    }

    render() {

        const {loading, info} = this.state

        let keys = Object.keys(info);
        return <Card loading={loading} title='连接池状体'>
            <Descriptions column={2} >
                {keys.map(k=>{
                    return <Item label={k} key={k}>{info[k]}</Item>
                })}
            </Descriptions>

        </Card>


    }


}



