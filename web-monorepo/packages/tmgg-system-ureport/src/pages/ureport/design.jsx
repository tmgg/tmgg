import React from 'react'
import {Card, Descriptions, Tabs} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

const Item = Descriptions.Item


export default class extends React.Component {

  state = {
    status: {}
  }

  componentDidMount() {
    HttpUtil.get('ureport/list').then(rs=>{

    })
  }

  render() {
    const status = this.state.status
    return <div >
       ureport
    </div>
  }


}



