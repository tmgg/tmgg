import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {theme} from "@tmgg/tmgg-base";
import Auth from "./Auth";



export class Layouts extends React.Component {

  state = {
    siteInfoLoaded: false,
    loginDataLoaded: false,
  }

  render() {
    return <ConfigProvider
      input={{autoComplete: 'off'}}
      form={{
        validateMessages: {
          required: '必填项'
        }
      }}

      theme={{
        token:{
          colorPrimary: theme["primary-color"],
          colorSuccess: theme["success-color"],
          colorWarning:theme["warning-color"],
          colorError: theme["error-color"],
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
