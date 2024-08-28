import React from "react";
import {Outlet} from "umi";
import {http, PageLoading, PageTool} from "@tmgg/tmgg-base";
import {Button, message} from "antd";

export default class extends React.Component {

    state = {
        tokenValid: undefined
    }

    componentDidMount() {
        http.get('/check-token').then(rs => {
            console.log('登录检查结果', rs.data)
            if (rs.data === false) {
                message.error('未登录')
                PageTool.open('/login')
            } else {
                message.success('已登录')
                this.setState({tokenValid:true})
            }
        })
    }

    render() {
        const pathname = PageTool.currentPathname();

        if(pathname === '/login'){
            return  <Outlet />
        }

        if (this.state.tokenValid === undefined) {
            return <Button>1</Button>
            //return <PageLoading message='登录检查...'/>
        }

        return  this.props.children
    }
}
