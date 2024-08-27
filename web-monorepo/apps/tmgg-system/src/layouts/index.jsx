import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import SimpleIFrameLayout from "./iframe/IFrameLayout";
import {sys, SysConfig} from "../common";
import LoginDataLoading from "./LoginDataLoading";
import {PageTool} from "@tmgg/tmgg-base";
import Auth from "./Auth";



export class Layouts extends React.Component {

  state = {
    siteInfoLoaded: false,
    loginDataLoaded: false,
  }

  render() {
    if(sys.isPageNeedLogin(window.location.pathname)){

    }

    return <ConfigProvider
      input={{autoComplete: 'off'}}
      form={{
        validateMessages: {
          required: '必填项'
        }
      }}
    >
      <Auth>
        <MenuLayout />
      </Auth>
    </ConfigProvider>
  }



}

export default Layouts
