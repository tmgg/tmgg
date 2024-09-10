import React from "react";
import {Loading} from "@taroify/core";
import './PageLoading.css'
/**
 * 页面加载中的动画
 */
export  class PageLoading extends React.Component {


    render() {
        const msg = this.props.message || '页面加载中...';
        return <div className='app-page-loaing' >
          <Loading  >{msg}
          </Loading>
        </div>
    }

}
