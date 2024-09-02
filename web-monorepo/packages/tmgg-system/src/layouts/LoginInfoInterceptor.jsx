import React from "react";
import {http, PageLoading, setDictInfo, setLoginInfo} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        loginInfoLoading:true,
        sysDictLoading:true
    }

    componentDidMount() {
        http.get('/getLoginInfo').then(rs => {
            setLoginInfo(rs)
            this.setState({loginInfoLoading:false})
        })

        http.get('sysDict/tree').then(rs => {
            setDictInfo(rs)
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
