import AdminLayout from "./admin"
import React from "react";

import {ConfigProvider, Modal} from "antd";
import {history, Outlet, withRouter} from "umi";
import zhCN from 'antd/locale/zh_CN';
import {ArrUtil, theme} from "@tmgg/tmgg-commons-lang";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


import './index.less'
import {HttpUtil, PageLoading, PageUtil, SysUtil} from "@tmgg/tmgg-base";

dayjs.locale('zh-cn');

/**
 * 属性列表：
 *
 * logo: 自定义的logo图片
 *
 */

// 不需要登录的页面
const SIMPLE_URLS = ['/login', '/test']

class _Layouts extends React.Component {


    state = {
        siteInfoLoading: true,
        loginInfoFinish: false
    }


    componentDidMount() {
        HttpUtil.get("/site-info").then(rs => {
            SysUtil.setSiteInfo(rs)
            this.setState({siteInfoLoading: false})

            this.loadLoginInfo()
        })
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        const pre = prevProps.location.pathname
        const cur = this.props.location.pathname

        if (pre !== cur) {
            this.loadLoginInfo()
        }
    }

    isSimplePage() {
        let {pathname} = this.props.location;
        return ArrUtil.contains(SIMPLE_URLS, pathname)
    }

    loadLoginInfo = () => {
        if (this.isSimplePage() || this.state.loginInfoFinish) {
            return;
        }

        Promise.all([
            HttpUtil.get('/checkLogin')
                .then(rs => {
                    const {isLogin, needUpdatePwd} = rs
                    if (isLogin && !needUpdatePwd) {
                        return;
                    }

                    if (needUpdatePwd) {
                        PageUtil.open('/userCenter/ChangePassword', '修改密码')
                        return;
                    }

                    // 缓存路径
                    let pathname = PageUtil.currentPathname();
                    localStorage.setItem("login_redirect_path", pathname)

                    Modal.error({
                        title:'检测登录失败',
                        content: '未登录，请先登录', ok: '去登录', onOk: this.reLogin
                    })
                }),
            HttpUtil.get('/getLoginInfo').then(res => {
                SysUtil.setLoginInfo(res)
            }),
            HttpUtil.get('sysDict/tree').then(res => {
                SysUtil.setDictInfo(res)
            }),

        ]).then(() => {
            this.setState({loginInfoFinish: true})
        })
    }


    reLogin = () => {
        HttpUtil.get('/logout').finally(() => {
            SysUtil.setToken(null)
            history.push('/login')
        })
    };


    renderContent = () => {
        if (this.state.siteInfoLoading) {
            return <PageLoading message='加载站点信息...'/>
        }
        let {params = {}} = this.props.location;
        let simple = this.isSimplePage();
        if (simple || params.hasOwnProperty('_noLayout')) {
            return <Outlet/>
        }

        if (!this.state.loginInfoFinish) {
            return <PageLoading message='加载登录信息...'/>
        }


        return <AdminLayout path={this.state.path} logo={this.props.logo}/>
    };


    render() {
        return <ConfigProvider
            input={{autoComplete: 'off'}}
            form={{
                validateMessages: {
                    required: '必填项'
                }, colon: false
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
            }}>

            {this.renderContent()}
        </ConfigProvider>
    }


}

export const Layouts = withRouter(_Layouts);
export default Layouts
export * from './PageRender'
