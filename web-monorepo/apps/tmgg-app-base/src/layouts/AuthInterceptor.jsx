import React from "react";
import Taro from "@tarojs/taro";
import {Empty} from "@taroify/core";
import {PageLoading} from "../components";
import {HttpUtil} from "../utils";

export  class AuthInterceptor extends React.Component {

    state = {
        tokenValid: undefined,
        errResult: undefined,
    }

    componentDidMount() {
        console.log("认证拦截器")
        HttpUtil.get('/check-token', null, {autoHandleErrors:false}).then(rs => {
            console.log("认证结果", rs)
            let tokenValid = rs;
            this.setState({tokenValid})
        }).catch((err)=>{
            this.setState({errResult: err})
        })
    }



    render() {
        const pathname = PageTool.currentPathname();
        if(pathname === '/login'){
            return this.props.children
        }

        if(this.state.errResult){
          return <Empty>
            <Empty.Image />
            <Empty.Description>{this.state.errResult.message}</Empty.Description>
          </Empty>
        }

        if (this.state.tokenValid === true) {
            return this.props.children
        }
        if (this.state.tokenValid === false) {
          return Taro.navigateTo({url:'/login'})
        }

        return <PageLoading message='登录检查...'/>

    }
}
