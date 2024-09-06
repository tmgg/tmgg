import React from 'react'
import {Button, Descriptions} from "antd";
import {HttpUtil, SysUtil} from "@tmgg/tmgg-base";

const Item = Descriptions.Item


export default class extends React.Component {

    state = {
        status: {}
    }

    componentDidMount() {
        HttpUtil.get('ureport/list').then(rs => {

        })
    }

    render() {
        const status = this.state.status
        return <div>
            <Button type='primary' target='_blank'
                    href={SysUtil.getServerUrl() + "ureport/designer"}>打开设计器</Button>
            ureport
        </div>
    }


}



