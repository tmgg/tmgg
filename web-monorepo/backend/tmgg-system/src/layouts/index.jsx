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

/**
 * 属性列表：
 *
 * logo: 自定义的logo图片
 *
 */
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
        let pathname = PageUtil.currentPathname();
        this.setState({pathname: pathname})
    }

    render() {
        return <ConfigProvider
            input={{autoComplete: 'off'}}
            form={{
                validateMessages: {
                    required: '必填项'
                },
                colon: false
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
        let pathname = this.state.pathname;
        if (pathname === '/login') {
            return <SiteInfoInterceptor>
                <Outlet/>
            </SiteInfoInterceptor>
        }
        if(pathname === '/test'){
            return <Outlet />
        }

        return <SiteInfoInterceptor>
            <AuthInterceptor>
                <LoginInfoInterceptor>
                    <MenuLayout pathname={pathname} {...this.props}/>
                </LoginInfoInterceptor>
            </AuthInterceptor>
        </SiteInfoInterceptor>
    }


}

export default Layouts
