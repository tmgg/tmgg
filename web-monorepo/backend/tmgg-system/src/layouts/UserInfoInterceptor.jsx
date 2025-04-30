import React from "react";
import {HttpSimpleUtil, HttpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";
import {Modal} from "antd";
import {history} from "umi";

export default class extends React.Component {

    state = {
        loginInfoLoading:true,
        sysDictLoading:true
    }

    componentDidMount() {
        console.log("登录信息拦截器")
        HttpSimpleUtil.get('/getLoginInfo').then(res => {
            const rs = res.data
            if(rs.success){
                SysUtil.setLoginInfo(rs.data)
                this.setState({loginInfoLoading:false})
            }else {
                Modal.error({
                    title: '获取登录信息失败',
                    content: rs.message,
                    okText:'重新登录',
                    onOk:()=>{
                        HttpUtil.get('/logout').finally(() => {
                            localStorage.clear()
                            history.replace('/login')
                        })
                    }
                })
            }

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
