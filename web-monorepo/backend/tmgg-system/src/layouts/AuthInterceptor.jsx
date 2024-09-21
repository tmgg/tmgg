import React from "react";
import { HttpUtil, PageLoading, PageUtil} from "@tmgg/tmgg-base";
import {Navigate} from "umi";
import {Button, Result} from "antd";

export default class extends React.Component {

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
        const pathname = PageUtil.currentPathname();


        if(pathname === '/login'){
            return this.props.children
        }

        if(this.state.errResult){
            return  <Result
                status="500"
                title={this.state.errResult.status}
                subTitle={this.state.errResult.message}
            />
        }

        if (this.state.tokenValid === true) {
            return this.props.children
        }
        if (this.state.tokenValid === false) {
            return <Navigate to="/login"></Navigate>
        }

        return <PageLoading message='登录检查...'/>

    }
}
