import React from "react";
import {SysConfig} from "../common";

export default class extends React.Component{



  componentDidMount() {
    console.log('LoginDataLoading 加载登录信息中...')
    SysConfig.loadLoginData().then(this.props.onAfterLogin)
  }



  render() {
    return <div>加载登录信息中...</div>
  }
}
