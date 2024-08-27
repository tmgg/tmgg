import React from "react";
import {Spin} from "antd";


export  class PageLoading extends React.Component {


    render() {
        return <div style={{height: '100%', width: '100%', display:'flex', alignItems:'center', justifyContent:"center"}}>

            <div>
            <Spin></Spin>
            页面加载中
            </div>
        </div>
    }

}