import React from "react";
import {http, PageLoading, PageTool} from "@tmgg/tmgg-base";
import {Navigate} from "umi";

export default class extends React.Component {

    state = {
        tokenValid: undefined
    }

    componentDidMount() {
        http.get('/check-token').then(rs => {
            let tokenValid = rs.data;
            this.setState({tokenValid})
        })
    }

    render() {
        const pathname = PageTool.currentPathname();

        console.log('auth.jsx', 'pathname', pathname, 'tokenValid',this.state.tokenValid)

        if(pathname === '/login'){
            return this.props.children
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
