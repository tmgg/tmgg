// 全局路由

import React from 'react';
import {Button, Divider, Layout, Menu} from 'antd';
import {history, Link, Outlet} from 'umi';
import "./index.less"
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'
import {ArrUtil, theme, TreeUtil} from "@tmgg/tmgg-commons-lang";

import HeaderRight from "./HeaderRight";

import TabMenu from "./TabMenu";
import {HttpUtil, PageUtil, SysUtil} from "@tmgg/tmgg-base";
import {MenuFoldOutlined, MenuUnfoldOutlined} from "@ant-design/icons";

const {Header, Footer, Sider, Content} = Layout;
/**
 * 带菜单的布局，主要处理布局宇框架结构
 */
export default class extends React.Component {

    state = {
        menus: [],
        topMenus: [],
        leftMenus: [],
        menuMap: {},

        tabs: [],

        currentTopMenuKey: null,
        currentMenuKey: null,


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
        HttpUtil.get('menuTree').then(menus => {
            let pathname = PageUtil.currentPathname();
            let currentMenuKey = null
            const menuMap = {}
            TreeUtil.traverseTree(menus, (item) => {
                let IconType = Icons[item.icon || 'SmileOutlined'];
                item.icon = <IconType style={{fontSize: 12}}/>
                menuMap[item.id] = item
                if (item.path === pathname) {
                    currentMenuKey = item.id
                }

            })
            const topMenus = menus.map(item => {
                const {key, label} = item
                return {key, label}
            })


            let currentTopMenuKey = null
            let leftMenus = []


            // 查早顶部菜单的当前key
            if (pathname === "" || pathname === "/") {
                currentTopMenuKey = menus[0]?.key
            } else {
                let menu = menuMap[currentMenuKey]
                while (menu && menu.pid){
                    menu = menuMap[menu.pid]
                }
                currentTopMenuKey = menu.id
            }

            leftMenus = menuMap[currentTopMenuKey].children

            this.setState({menus, menuMap, topMenus, leftMenus, currentTopMenuKey, currentMenuKey})
        })
    }
    actionRef = React.createRef()


    addTab = (key, path, label, icon) => {
        const {tabs} = this.state
        if (!tabs.some(t => t.key === key)) {
            tabs.push({key, path, label, icon})
        }
    }
    removeTab = (item) => {
        const tabs = this.state.tabs
        ArrUtil.remove(tabs, item)
        this.setState({tabs})
        history.push(tabs[0]?.path || '/')
    }


    render() {
        const {siteInfo, topMenus} = this.state
        return <Layout className='main-layout'>
            <Header className='header'>
                <div className='header-left'>
                    <img className='logo-img' src={logo} onClick={() => history.push('/')}/>
                    <Divider type='vertical'/>
                    <h3>
                        <Link to="/" style={{color: theme["primary-color"]}}>{siteInfo.title} </Link>
                    </h3>

                    <Menu items={topMenus}
                          mode="horizontal"
                          selectedKeys={[this.state.currentTopMenuKey]}
                          onClick={e => {
                              let currentTopMenuKey = e.key;
                              const leftMenus = this.state.menuMap[currentTopMenuKey].children
                              this.setState({currentTopMenuKey, leftMenus})
                          }}
                          style={{lineHeight: '42px'}}
                    ></Menu>
                </div>
                <HeaderRight></HeaderRight>
            </Header>

            <Layout style={{height: '100%'}}>
                <Sider id='left-sider' collapsible collapsed={this.state.collapsed}
                       onCollapse={(value) => this.toggleCollapsed(value)}>

                    <div style={{
                        color:'white',fontSize:16, cursor:"pointer", margin:12}} title='收起/展开' onClick={()=>this.toggleCollapsed(!this.state.collapsed)}>
                      {this.state.collapsed ? <MenuUnfoldOutlined/> : <MenuFoldOutlined/>}
                    </div>

                    <Menu items={this.state.leftMenus}
                          theme='dark'
                          mode="inline"
                          className='left-menu'
                          onClick={({key, item}) => {
                              let {path, id} = item.props;
                              let menu = this.state.menuMap[id]
                              this.addTab(key, path, menu.label, menu.icon)
                              this.setState({currentMenuKey: id})
                              history.push(path)
                          }}
                          selectedKeys={[this.state.currentMenuKey]}
                    >
                    </Menu>

                </Sider>

                <Content id='content'>
                    <TabMenu items={this.state.tabs}
                             pathname={this.props.pathname}
                             onTabRemove={this.removeTab}>
                    </TabMenu>

                    <div className='tab-content'>
                        <Outlet/>
                    </div>
                </Content>

            </Layout>
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
    }


}


