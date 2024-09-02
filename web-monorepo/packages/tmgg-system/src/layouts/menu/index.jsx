// 全局路由

import React from 'react';
import {Badge, Breadcrumb, Card, Dropdown, Layout, Menu, Segmented, Slider, Tabs} from 'antd';
import {history, Link, Outlet} from 'umi';
import "./index.less"
import '../../css/style.less'
import '../../css/table.less'
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'

import {PageLoading, ProLayout} from "@ant-design/pro-components";

import HeaderRight from "./HeaderRight";
import {HttpClient, sys, SysConfig, TreeUtil, uid} from "../../common";
import hutool from "@moon-cn/hutool";
import {arr, getSiteInfo, PageTool, theme} from "@tmgg/tmgg-base";
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

        tabs: [],

        selectedTabKey: null,

        dataLoaded: false,

        collapsed: false,

        siteInfo:{}
    }


    toggleCollapsed = (v) => {
        this.setState({collapsed: v})
    }

    componentDidMount() {

        this.initMenu()

        SysConfig.loadLoginData().then(() => {
            this.setState({dataLoaded: true})
        })

        this.setState({siteInfo: getSiteInfo()})

    }


    initMenu = () => {

        HttpClient.get('menuTree').then(rs => {
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

        })
    }
    actionRef = React.createRef()






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
            <Sider id='left-sider' collapsible collapsed={this.state.collapsed}
                   onCollapse={(value) => this.toggleCollapsed(value)}>
                <div className='logo' onClick={() => history.push('/')}>
                    <img src={logo}/>
                </div>
                <LeftMenu pathname={this.props.pathname} onSelect={this.onMenuSelect}/>
            </Sider>
            <Layout style={{height:'100%'}}>
                <Header className='header'>
                    <h3 style={{color: theme["primary-color"]}}>{this.state.siteInfo.title}</h3>
                    <HeaderRight></HeaderRight>
                </Header>

                <Content id='content'>
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
                        margin:0,
                        padding:12
                    }}
                >
                    {this.state.siteInfo.footer}
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


