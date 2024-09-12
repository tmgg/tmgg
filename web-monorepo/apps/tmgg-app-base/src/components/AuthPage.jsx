import React from "react";
import {SysUtil} from "@tmgg/tmgg-common";
import Taro from "@tarojs/taro";

export class AuthPage extends React.Component {



    render() {
        let loginStatus = SysUtil.getLoginStatus();
        if (loginStatus ===1) {
            return this.props.children
        }
        Taro.redirectTo({url:'/pages/login/index'})
    }


}