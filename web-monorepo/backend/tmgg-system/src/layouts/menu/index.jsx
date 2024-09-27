// 全局路由

import React from 'react';
import {Layout} from 'antd';
import {history, Outlet, Link} from 'umi';
import "./index.less"
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'
import {theme} from "@tmgg/tmgg-commons-lang";

import HeaderRight from "./HeaderRight";

import TabMenu from "./TabMenu";
import LeftMenu from "./LeftMenu";
import { HttpUtil, SysUtil} from "@tmgg/tmgg-base";
import {ArrUtil, TreeUtil} from "@tmgg/tmgg-commons-lang";

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


        collapsed: false,
        siteInfo: {}
    }


    toggleCollapsed = (v) => {
        this.setState({collapsed: v})
    }

    componentDidMount() {

        this.initMenu()
        let siteInfo = SysUtil.getSiteInfo();

        this.setState({siteInfo})

    }


    initMenu = () => {
        HttpUtil.get('menuTree').then(rs => {
            const list = rs;
            // 设置icon
            TreeUtil.traverseTree(list, (item) => {
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
        const {tabs} = this.state

        if (!tabs.some(t => t.key === key)) {
            tabs.push({key, path, label, icon})
        }
    }

    render() {
        const {siteInfo} = this.state
        return <Layout className='main-layout'>
            <Sider id='left-sider' collapsible collapsed={this.state.collapsed}
                   onCollapse={(value) => this.toggleCollapsed(value)}>
                <div className='logo' onClick={() => history.push('/')}>
                    <img src={logo}/>
                </div>
                <LeftMenu pathname={this.props.pathname} onSelect={this.onMenuSelect}/>
            </Sider>
            <Layout style={{height: '100%'}}>
                <Header className='header'>

                    <h3 >
                        <Link to="/" style={{color: theme["primary-color"]}}>{siteInfo.title} </Link>
                    </h3>
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
                        margin: 0,
                        padding: 12
                    }}
                >
                    {siteInfo.copyright}
                </Footer>
            </Layout>
        </Layout>
    }


    onTabRemove = (item) => {
        const tabs = this.state.tabs
        ArrUtil.remove(tabs, item)
        this.setState({tabs})
        history.push(tabs[0]?.path || '/')
    }

    renderIframe() {
        return <iframe
            src={sys.appendTokenToUrl(path)}
            style={{height: 'calc(100vh - 100px)'}}
            sandbox="allow-scripts allow-same-origin allow-forms"
        ></iframe>
    }
}


