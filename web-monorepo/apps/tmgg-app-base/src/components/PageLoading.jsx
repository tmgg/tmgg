import React from "react";
import './PageLoading.css'
import {View} from "@tarojs/components";
import {Loading} from "@antmjs/vantui";
/**
 * 页面加载中的动画
 */
export  class PageLoading extends React.Component {


    render() {
        const msg = this.props.message || '页面加载中...';
        return <View className='app-page-loaing' >
          <Loading type={"spinner"}>{msg}</Loading>
        </View>
    }

}
