import React from "react";
import {Spin} from "antd";
import {theme} from "../system/theme";

/**
 * 页面加载中的动画
 */
export  class PageLoading extends React.Component {


    render() {
        return <div style={{height: '100vh', width: '100%',
            display:'flex', alignItems:'center', justifyContent:"center",
            color:theme["primary-color"]
        }}>
            <div style={{textAlign:"center", marginTop:'-10rem'}}>
                <div>
                    <Spin size={"large"}></Spin>
                </div>
                <div style={{marginTop:'1rem'}}>
                    {this.props.message || '页面加载中...'}
                </div>
            </div>
        </div>
    }

}