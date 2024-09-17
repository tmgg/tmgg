import React from "react";
import Taro from "@tarojs/taro";
import {SysUtil} from "@tmgg/tmgg-commons-lang";

export class AuthPage extends React.Component {



    render() {
        let loginStatus = SysUtil.getLoginStatus();
        if (loginStatus ===1) {
            return this.props.children
        }
        Taro.redirectTo({url:'/pages/login/index'})
    }


}