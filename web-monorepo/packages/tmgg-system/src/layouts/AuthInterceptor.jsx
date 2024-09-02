import React from "react";
import { HttpUtil, PageLoading, PageTool} from "@tmgg/tmgg-base";
import {Navigate} from "umi";
import {Button, Result} from "antd";

export default class extends React.Component {

    state = {
        tokenValid: undefined,
        errResult: undefined,
    }

    componentDidMount() {
        HttpUtil.get('/check-token', null, {autoShowErrorMessage:false}).then(rs => {
            let tokenValid = rs;
            this.setState({tokenValid})
        }).catch((err)=>{
            this.setState({errResult: err})
        })
    }



    render() {
        const pathname = PageTool.currentPathname();

        console.log('auth.jsx', 'pathname', pathname, 'tokenValid',this.state.tokenValid)

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

        switch (this.state.tokenValid) {
            case true:
                return  this.props.children
            case false:
                return <Navigate to="/login"></Navigate>
            default:
                return <PageLoading message='登录检查...'/>
        }

    }
}
