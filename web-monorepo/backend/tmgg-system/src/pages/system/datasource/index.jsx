import {Card, Descriptions} from 'antd';
import React from 'react';


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
        return <Card loading={loading} title='连接池状态'>
            <Descriptions column={1} labelStyle={{width:'200px'}} colon={false} >
                {keys.map(k=>{
                    return <Item label={k} key={k}>{info[k]}</Item>
                })}
            </Descriptions>

        </Card>


    }


}



