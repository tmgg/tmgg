import React from "react";
import {Outlet} from "umi";
import {http,PageLoading} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        isLogin: undefined
    }

    componentDidMount() {
        http.get('/login-check').then(rs=>{
            console.log('登录结果',rs)

        })
    }

    render() {

        if(this.state.isLogin === undefined){
          return  <PageLoading />
        }


        return <>
            <Outlet />
        </>
    }
}