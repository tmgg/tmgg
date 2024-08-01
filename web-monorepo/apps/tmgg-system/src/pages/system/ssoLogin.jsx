import React from "react";

import {sys, SysConfig} from "../../common";
import {message} from "antd";
import {history} from "umi";

export default class extends React.Component {

  componentDidMount() {
    let ssoToken = this.props.location.query?.ssoToken;
    if (ssoToken) { // 登录
      localStorage.setItem(sys.AUTH_TOKEN_NAME, ssoToken)

      const hide = message.loading("加载登录数据中")
      SysConfig.loadLoginData().then(() => {
        console.log("跳转到首页....")
        hide();
        history.push('/')
        window.location = "/"
      })
    }else {
      window.location.href ='/sso/goto?oriUrl=' + encodeURIComponent( location.href);
    }
  }

  render() {
    return <div>登录中...</div>
  }
}
