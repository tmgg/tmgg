import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {PageUtil} from "@tmgg/tmgg-base";
import AuthInterceptor from "./AuthInterceptor";
import {history, Outlet} from "umi";
import zhCN from 'antd/locale/zh_CN';
import {theme, UrlUtil} from "@tmgg/tmgg-commons-lang";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


import './index.less'
import SiteInfoInterceptor from "./SiteInfoInterceptor";
import UserInfoInterceptor from "./UserInfoInterceptor";

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

        pathname: null, // 不带参路径
        path: null // 带参路径
    }

    componentDidMount() {
        this.unlisten = history.listen(({location, action}) => {
            console.log('监听路由：不带参路径为',location);
            let path = location.pathname + location.search;
            console.log('带参数', path)
            this.setState({pathname: location.pathname, path: path})
        });
        let pathname = PageUtil.currentPathname();
        let path = PageUtil.currentPath();
        this.setState({pathname, path})
    }
    componentWillUnmount() {
        this.unlisten()
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
                <UserInfoInterceptor>
                    <MenuLayout pathname={pathname} path={this.state.path} {...this.props}/>
                </UserInfoInterceptor>
            </AuthInterceptor>
        </SiteInfoInterceptor>
    }


}

export default Layouts
