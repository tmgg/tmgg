import React from "react";
import {HttpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        loading:true
    }

    componentDidMount() {
        console.log('SiteInfo拦截器')
        HttpUtil.get("/site-info").then(rs=>{
            SysUtil.setSiteInfo(rs)
            this.setState({loading: false})
        })
    }


    render() {
        if (this.state.loading === true) {
            return <PageLoading message='加载站点数据中...'/>
        }

        return this.props.children
    }
}
