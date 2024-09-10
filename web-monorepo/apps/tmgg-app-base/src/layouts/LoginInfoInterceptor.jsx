import React from "react";
import {HttpUtil} from "../utils";
import {SysUtil} from "@tmgg/tmgg-common";
import {PageLoading} from "../components";

export  class AuthInterceptor extends React.Component {

    state = {
        loginInfoLoading:true,
        sysDictLoading:true
    }

    componentDidMount() {
        console.log("登录信息拦截器")
        HttpUtil.get('/getLoginInfo').then(rs => {
            SysUtil.setLoginInfo(rs)
            this.setState({loginInfoLoading:false})
        })

        HttpUtil.get('sysDict/tree').then(rs => {
            SysUtil.setDictInfo(rs)
            this.setState({sysDictLoading:false})
        })
    }


    render() {
        if (this.state.loginInfoLoading || this.state.sysDictLoading) {
            return <PageLoading message='加载用户数据中...'/>
        }

        return this.props.children
    }
}
