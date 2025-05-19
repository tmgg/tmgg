import AdminLayout from "./admin"
import React from "react";

import {ConfigProvider, Modal} from "antd";
import {Outlet, withRouter} from "umi";
import zhCN from 'antd/locale/zh_CN';
import {ObjUtil, theme, UrlUtil} from "@tmgg/tmgg-commons-lang";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


import './index.less'
import './global.less'
import {InterceptorWrapper} from "./InterceptorWrapper";
import {PageRender} from "./PageRender";

dayjs.locale('zh-cn');

/**
 * 属性列表：
 *
 * logo: 自定义的logo图片
 *
 */
class _Layouts extends React.Component {

    state = {


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
            button={{
                autoInsertSpace: false
            }}
            locale={zhCN}
            theme={{
                token: {
                    colorPrimary: theme["primary-color"],
                    colorSuccess: theme["success-color"],
                    colorWarning: theme["warning-color"],
                    colorError: theme["error-color"],
                    borderRadius: 4,

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


            <Modal ></Modal>

        </ConfigProvider>
    }


    renderContent = () => {
        let {pathname, search, match} = this.props.location;
        let params = UrlUtil.getParams(search);

        if (match && match.params) {
            // 复制动态路由参数，那种基于路径的
            ObjUtil.copyProperty(match.params, params);
        }


        const location = {pathname, search, params}
        if (pathname === '/login') {
            return <InterceptorWrapper siteInfoOnly pathname={pathname}>
                <Outlet/>
            </InterceptorWrapper>
        }
        if (pathname === '/test') {
            return <Outlet/>
        }

        if (params.hasOwnProperty('_noLayout')) {
            return <PageRender {...location}/>
        }

        return <InterceptorWrapper pathname={pathname}>
            <AdminLayout path={this.state.path} logo={this.props.logo}/>
        </InterceptorWrapper>
    };



}

export const Layouts = withRouter(_Layouts);
export default Layouts
export  * from './PageRender'
