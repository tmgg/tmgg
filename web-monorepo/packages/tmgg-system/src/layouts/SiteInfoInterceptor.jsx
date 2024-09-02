import React from "react";
import {http, PageLoading, setSiteInfo} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        loading:true
    }

    componentDidMount() {
        httpUtil.get("/site-info").then(rs=>{
            setSiteInfo(rs)
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
