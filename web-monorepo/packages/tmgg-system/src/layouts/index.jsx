import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {theme} from "@tmgg/tmgg-base";
import Auth from "./Auth";
import {Outlet,history} from "umi";



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
