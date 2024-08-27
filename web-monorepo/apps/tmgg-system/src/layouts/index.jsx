import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import SimpleIFrameLayout from "./iframe/IFrameLayout";
import {sys, SysConfig} from "../common";
import LoginDataLoading from "./LoginDataLoading";



export class Layouts extends React.Component {

  state = {
    siteInfoLoaded: false,
    loginDataLoaded: false,
  }

  render() {
    if(sys.isPageNeedLogin(window.location.pathname)){

    }


    if(this.isMenuLayout() && !this.state.loginDataLoaded && SysConfig.isPageNeedLogin(window.location.pathname) ){
      return  <LoginDataLoading onAfterLogin={()=>{
        console.log("layout index:", "afterLogin")
        this.setState({loginDataLoaded:true})
      }} />
    }

    return <ConfigProvider
      input={{autoComplete: 'off'}}
      form={{
        validateMessages: {
          required: '必填项'
        }
      }}
    >
        {this.renderChildren()}
    </ConfigProvider>
  }


  renderChildren() {
    const {children} = this.props;

    const {search} = window.location;

    // 菜单布局
    if (this.isMenuLayout()) {
      return <MenuLayout location={location}>{children}</MenuLayout>
    }

    // iframe 不带
    // 如果参数中 类似  /user?layout=iframe
    if (search.layout === 'iframe') {
      return <SimpleIFrameLayout location={location}>{children}</SimpleIFrameLayout>
    }

    return children;
  }


}

export default Layouts
