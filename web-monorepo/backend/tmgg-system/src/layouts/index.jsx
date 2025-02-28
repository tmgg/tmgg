import MenuLayout from "./menu"
import React from "react";

import {ConfigProvider} from "antd";
import {PageUtil} from "@tmgg/tmgg-base";
import AuthInterceptor from "./AuthInterceptor";
import {history, Outlet, withRouter} from "umi";
import zhCN from 'antd/locale/zh_CN';
import {theme} from "@tmgg/tmgg-commons-lang";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


import './index.less'
import SiteInfoInterceptor from "./SiteInfoInterceptor";
import UserInfoInterceptor from "./UserInfoInterceptor";
import MyPureOutlet from "./MyPureOutlet";

dayjs.locale('zh-cn');

/**
 * 属性列表：
 *
 * logo: 自定义的logo图片
 *
 */
 class _Layouts extends React.Component {

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


    renderContent = () => {
        let pathname = this.props.location.pathname;
        if (pathname === '/login') {
            return <SiteInfoInterceptor>
                <Outlet/>
            </SiteInfoInterceptor>
        }
        if(pathname === '/test'){
            return <Outlet />
        }

        const params =PageUtil.currentParams()

        if(params.hasOwnProperty('_pure')){
            return  <MyPureOutlet pathname={pathname} />
        }

        return <SiteInfoInterceptor>
            <AuthInterceptor>
                <UserInfoInterceptor>
                    <MenuLayout pathname={pathname} path={this.state.path} {...this.props}/>
                </UserInfoInterceptor>
            </AuthInterceptor>
        </SiteInfoInterceptor>
    };


}

export const Layouts = withRouter(_Layouts);
export default Layouts
