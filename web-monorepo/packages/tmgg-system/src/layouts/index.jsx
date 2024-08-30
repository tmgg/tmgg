import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {http, PageTool, theme} from "@tmgg/tmgg-base";
import Auth from "./Auth";
import {Outlet,history} from "umi";
import zhCN from 'antd/locale/zh_CN';

import './index.less'
import {sys} from "../common";

http.axiosInstance.defaults.baseURL = sys.getServerUrl()

export class Layouts extends React.Component {

  state = {
    siteInfoLoaded: false,
    loginDataLoaded: false,

    pathname: null
  }

  componentDidMount() {
    console.log('************************进入主布局************************')

    const unlisten = history.listen(({ location, action }) => {
      console.log(location.pathname);
      this.setState({pathname: location.pathname})
    });
    this.setState({pathname: PageTool.currentPathname()})
  }

  render() {
    return <ConfigProvider
      input={{autoComplete: 'off'}}
      form={{
        validateMessages: {
          required: '必填项'
        }
      }}
      locale={zhCN}
      theme={{
        token:{
          colorPrimary: theme["primary-color"],
          colorSuccess: theme["success-color"],
          colorWarning:theme["warning-color"],
          colorError: theme["error-color"],
        },
        components:{
          Menu:{
            darkItemBg: theme["primary-color"],
            darkPopupBg: theme["primary-color"],
            darkItemSelectedBg: theme["primary-color-hover"],
            darkItemHoverBg: theme["primary-color-click"],
            darkSubMenuItemBg: theme["primary-color"]
          },
          Layout: {
            siderBg: theme["primary-color"],
            triggerBg: theme["primary-color-click"],
            headerBg: 'white',
            triggerHeight:32
          }
        }
      }}


    >

        {this.renderContent()}

    </ConfigProvider>
  }


  renderContent(){
    console.log('renderContent', this.state.pathname)

    if(this.state.pathname === '/login'){
      return  <Outlet />
    }

    return  <Auth>
      <MenuLayout pathname={this.state.pathname} />
    </Auth>
  }


}

export default Layouts
