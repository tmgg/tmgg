// 全局路由

import React from 'react';
import {Badge, Breadcrumb, Card, Dropdown, Layout, Menu, Segmented, Slider, Tabs} from 'antd';
import {history, Link, Outlet} from 'umi';
import "./antd_ext.less"
import "./index.less"
import '../../css/style.less'
import '../../css/table.less'
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'

import {PageLoading, ProLayout} from "@ant-design/pro-components";

import HeaderRight from "./HeaderRight";
import {HttpClient, sys, SysConfig, TreeUtil, uid} from "../../common";
import hutool from "@moon-cn/hutool";
import {arr, PageTool, theme} from "@tmgg/tmgg-base";
import TabMenu from "./TabMenu";
import LeftMenu from "./LeftMenu";

const {Header, Footer, Sider, Content} = Layout;
/**
 * 带菜单的布局，主要处理布局宇框架结构
 */
export default class extends React.Component {

    state = {
        list: [],
        menus: [],

        currentAppKey: null,

        tabs: [],

        selectedTabKey: null,

        dataLoaded: false,

        collapsed: false
    }


    toggleCollapsed = (v) => {
        this.setState({collapsed: v})
    }

    componentDidMount() {

        this.initMenu()

        SysConfig.loadLoginData().then(() => {
            this.setState({dataLoaded: true})
        })

    }


    initMenu = () => {

        HttpClient.get('appMenuTree').then(rs => {
            const list = rs.data;
            // 设置icon
            TreeUtil.every(list, (item) => {
                let IconType = Icons[item.icon || 'SmileOutlined'];
                item.icon = <IconType style={{fontSize: 12}}/>

                if (item.path) {
                    if (item.iframe) {
                        item.iframePath = item.path;

                        // pro layout的bug， 如果http开头的，会直接打开新窗口
                        if (item.path.startsWith('http')) {
                            item.path = '/' + item.path;
                        }
                    }
                }

            })
            this.setState({list}, () => this.changeApp(list[0]))

        })
    }
    actionRef = React.createRef()

    /**
     *
     * @param item
     * @param forceNewTab
     */
    openTab = (item) => {
        let {id, path} = item;
        const {tabs} = this.state;

        if (!path) {
            return;
        }

        // 没有id，说明是页面内打开的， 而非通过点击菜单打开
        if (id == null) {
            item.id = id = uid()
        }

        // 判断页签是否打开
        const exist = tabs.some(t => t.id === id)
        if (!exist) {
            tabs.push(item)
        }
        this.setState({tabs, selectedTabKey: id})

        PageTool.open(path)
    }


    changeApp = app => {
        if (app) {
            this.setState({menus: app.children, currentAppKey: app.key}, this.actionRef.current?.reload)
        }
    }

    onMenuSelect = (key, path, label, icon) => {
        console.log(key, path, label)
        const {tabs} = this.state

        if (!tabs.some(t => t.key === key)) {
            tabs.push({key, path, label, icon})
        }
    }

    render() {
        console.log('开始渲染 menu index.jsx')
        if (!this.state.dataLoaded) {
            return <PageLoading/>
        }

        const title = sys.getSiteInfo().siteTitle || '未定义标题'


        return <Layout className='main-layout'>
            <Sider collapsible collapsed={this.state.collapsed}
                   onCollapse={(value) => this.toggleCollapsed(value)}>
                <div className='logo' onClick={() => history.push('/')}>
                    <img src={logo}/>
                </div>
                <LeftMenu pathname={this.props.pathname} onSelect={this.onMenuSelect}/>
            </Sider>
            <Layout>
                <Header className='header'>
                    <h3 style={{color: theme["primary-color"]}}>{title}</h3>
                    <HeaderRight></HeaderRight>
                </Header>

                <Content className='content'>
                    <TabMenu items={this.state.tabs}
                             pathname={this.props.pathname}
                             onTabRemove={this.onTabRemove}
                    >
                    </TabMenu>

                    <div className='tab-content'>
                        <Outlet/>
                    </div>

                </Content>
                <Footer
                    style={{
                        textAlign: 'center',
                    }}
                >
                    Tmgg Design ©{new Date().getFullYear()} Created by Mxvc
                </Footer>
            </Layout>
        </Layout>
    }


    onTabRemove = (item) => {
        const tabs = this.state.tabs

        arr.remove(tabs, item)
        this.setState({tabs})
        history.push(tabs[0]?.path || '/')
    }

    renderIframe() {
        return <iframe
            {...hutool.html.getIframeCommonProps()}
            src={sys.appendTokenToUrl(path)}
            style={{height: 'calc(100vh - 100px)'}}
            sandbox="allow-scripts allow-same-origin allow-forms"
        ></iframe>
    }
}


