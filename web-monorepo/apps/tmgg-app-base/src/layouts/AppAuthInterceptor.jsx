import React from "react";
import Taro from "@tarojs/taro";
import {Empty} from "@taroify/core";
import {PageLoading} from "../components";
import {HttpUtil} from "../utils";
import {View} from "@tarojs/components";

export  class AppAuthInterceptor extends React.Component {

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
        const pathname = Taro.getCurrentInstance().router.path;
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
           Taro.navigateTo({url:'/login'})

            return <View>登录凭证失效</View>
        }

        return <PageLoading message='登录检查...'/>

    }
}
