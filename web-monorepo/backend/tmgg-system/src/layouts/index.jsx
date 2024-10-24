import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {PageUtil} from "@tmgg/tmgg-base";
import AuthInterceptor from "./AuthInterceptor";
import {history, Outlet} from "umi";
import zhCN from 'antd/locale/zh_CN';
import {theme} from "@tmgg/tmgg-commons-lang";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


import './index.less'
import SiteInfoInterceptor from "./SiteInfoInterceptor";
import LoginInfoInterceptor from "./LoginInfoInterceptor";

dayjs.locale('zh-cn');

export class Layouts extends React.Component {

    state = {
        siteInfoLoaded: false,
        loginDataLoaded: false,

        pathname: null
    }

    componentDidMount() {
        const unlisten = history.listen(({location, action}) => {
            console.log(location.pathname);
            this.setState({pathname: location.pathname})
        });
        this.setState({pathname: PageUtil.currentPathname()})
    }

    render() {
        return <ConfigProvider
            input={{autoComplete: 'off'}}
            form={{
                validateMessages: {
                    required: '必填项'
                },
                colon:false
            }}

            locale={zhCN}
            theme={{
                token: {
                    colorPrimary: theme["primary-color"],
                    colorSuccess: theme["success-color"],
                    colorWarning: theme["warning-color"],
                    colorError: theme["error-color"],
                },
                components: {
                    Menu: {
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
                        triggerHeight: 32
                    },
                }
            }}


        >

            {this.renderContent()}

        </ConfigProvider>
    }


    renderContent() {
        if (this.state.pathname === '/login') {
            return <SiteInfoInterceptor>
                <Outlet/>
            </SiteInfoInterceptor>
        }

        return <>
            <SiteInfoInterceptor>
                <AuthInterceptor>
                    <LoginInfoInterceptor>
                        <MenuLayout pathname={this.state.pathname}/>
                    </LoginInfoInterceptor>
                </AuthInterceptor>
            </SiteInfoInterceptor>
        </>
    }


}

export default Layouts
