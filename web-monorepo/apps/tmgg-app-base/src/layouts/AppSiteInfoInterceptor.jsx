import React from "react";
import {HttpUtil} from "../utils";
import {PageLoading} from "../components";
import {SysUtil} from "@tmgg/tmgg-common";

export  class AppSiteInfoInterceptor extends React.Component {

    state = {
        loading:true
    }

    componentDidMount() {
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
