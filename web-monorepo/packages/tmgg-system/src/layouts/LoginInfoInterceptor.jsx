import React from "react";
import {httpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        loginInfoLoading:true,
        sysDictLoading:true
    }

    componentDidMount() {
        httpUtil.get('/getLoginInfo').then(rs => {
            SysUtil.setLoginInfo(rs)
            this.setState({loginInfoLoading:false})
        })

        httpUtil.get('sysDict/tree').then(rs => {
            SysUtil.setDictInfo(rs)
            this.setState({sysDictLoading:false})
        })
    }


    render() {
        if (this.state.loading === true) {
            return <PageLoading message='加载用户数据中...'/>
        }

        return this.props.children
    }
}
