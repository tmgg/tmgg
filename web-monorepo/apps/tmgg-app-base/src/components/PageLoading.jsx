import React from "react";
import {Loading} from "@taroify/core";

/**
 * 页面加载中的动画
 */
export  class PageLoading extends React.Component {


    render() {
        const msg = this.props.message || '页面加载中...';

        return <div style={{height: '100vh', width: '100%',
            display:'flex', alignItems:'center', justifyContent:"center",
        }}>
          <Loading  >{msg}
          </Loading>
        </div>
    }

}
