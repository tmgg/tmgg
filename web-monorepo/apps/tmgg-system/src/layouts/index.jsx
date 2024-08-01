import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider, Spin} from "antd";
import SimpleIFrameLayout from "./iframe/IFrameLayout";
import {LibValueType, SysConfig} from "../common";
import LoginDataLoading from "./LoginDataLoading";
import {toggleWatermark} from "./watermark";
import {ProProvider} from "@ant-design/pro-components";



export class Layouts extends React.Component {

  state = {
    siteInfoLoaded: false,
    loginDataLoaded: false,
  }

  componentDidMount() {
    console.log('顶层布局页面加载，初始化一些站点数据...')

    this.loadSiteInfo().then((msg) => {
      this.setState({siteInfoLoaded: true, msg})
    }).catch(msg => {
      this.setState({msg})
    })

    toggleWatermark(this.props.location.pathname)
  }


  loadSiteInfo = () => {
    if (SysConfig.getSiteInfo() != null) {
      return Promise.resolve()
    }
    return SysConfig.loadSiteInfo()
  }

  render() {
    if (!this.state.siteInfoLoaded) {
      return <div><Spin/> 站点信息加载中...</div>
    }

    if(this.isMenuLayout() && !this.state.loginDataLoaded && SysConfig.isPageNeedLogin(this.props.location.pathname) ){
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
      <ProProvider.Provider
        value={{
          valueTypeMap: LibValueType,
        }}
      >
        {this.renderChildren()}
      </ProProvider.Provider>
    </ConfigProvider>
  }


  renderChildren() {
    const {children, location} = this.props;

    const {query} = location;

    // 菜单布局
    if (this.isMenuLayout()) {
      return <MenuLayout location={location}>{children}</MenuLayout>
    }

    // iframe 不带
    // 如果参数中 类似  /user?layout=iframe
    if (query.layout === 'iframe') {
      return <SimpleIFrameLayout location={location}>{children}</SimpleIFrameLayout>
    }

    return children;
  }

  isMenuLayout() {
    const {children, location} = this.props;

    const {pathname, query} = location;
    return pathname === "" || pathname === "/";
  }
}

export default Layouts
