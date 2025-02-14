// 全局路由

import React from 'react';
import {Divider, Layout, Menu} from 'antd';

import {history, Link} from 'umi';
import "./index.less"
import * as Icons from '@ant-design/icons';
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import defaultLogo from '../../asserts/logo.png'
import {ArrUtil, DateUtil, isMobileDevice, theme, TreeUtil} from "@tmgg/tmgg-commons-lang";

import HeaderRight from "./HeaderRight";

import TabMenu from "./TabMenu";
import {HttpUtil, PageUtil, SysUtil} from "@tmgg/tmgg-base";
import {AliveScope} from "react-activation";

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
        siteInfo: {},

        isMobileDevice: false
    }


    toggleCollapsed = (v) => {
        this.setState({collapsed: v})
    }

    componentDidMount() {
        this.initMenu()
        let siteInfo = SysUtil.getSiteInfo();
        this.setState({siteInfo})

        // 判断是否手机端，自动收起菜单
        if (isMobileDevice()) {
            this.setState({collapsed: true, isMobileDevice: true})
        }

    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        let curPath = this.props.path;
        let curPathname = this.props.pathname
        if (prevProps.path !== curPath) {
            const tabs = this.state.tabs
            const curTab = tabs.find(t => t.path === curPath)
            if (curTab == null) {
                const label = PageUtil.currentLabel()
                this.addTab(curPath, curPath, label)
            }
        }
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

                    if(this.state.tabs.length === 0) {
                        this.addTab(item.id, item.path, item.label)
                    }

                }
            })
            const topMenus = menus.map(item => {
                const {key, label} = item
                return {key, label}
            })


            let currentTopMenuKey = null
            let leftMenus = []


            // 查早顶部菜单的当前key
            currentTopMenuKey = menus[0]?.key

            if (pathname !== "" && pathname !== "/") {
                let menu = menuMap[currentMenuKey]
                while (menu && menu.pid) {
                    menu = menuMap[menu.pid]
                }
                if (menu) {
                    currentTopMenuKey = menu.id
                }
            }

            leftMenus = menuMap[currentTopMenuKey]?.children

            this.setState({menus, menuMap, topMenus, leftMenus, currentTopMenuKey, currentMenuKey})
        })
    }
    actionRef = React.createRef()


    addTab = (key, path, label, icon) => {
        const {tabs} = this.state
        if (!tabs.some(t => t.key === key)) {
            tabs.push({key, path, label, icon})
            this.setState({tabs})
        }
    }
    removeTab = (item) => {
        const tabs = this.state.tabs
        ArrUtil.remove(tabs, item)
        this.setState({tabs})
        history.push(tabs[0]?.path || '/')
    }


    render() {
        let logo = this.props.logo || defaultLogo
        const {siteInfo, topMenus} = this.state
        return <Layout className='main-layout'>
            <Header className='header'>
                <div className='header-left'>

                    <img className='logo-img' src={logo} onClick={() => history.push('/')}/>
                    {!this.state.isMobileDevice && <>
                        <h3>
                            <Link to="/" style={{color: theme["primary-color"]}}>{siteInfo.title} </Link>
                        </h3>
                    </>}


                    <Menu items={topMenus}
                          mode="horizontal"
                          selectedKeys={[this.state.currentTopMenuKey]}
                          onClick={e => {
                              let currentTopMenuKey = e.key;
                              let topMenu = this.state.menuMap[currentTopMenuKey];
                              const leftMenus = topMenu.children
                              if (topMenu) {
                                  this.setState({currentTopMenuKey, leftMenus})
                              }
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
                        color: 'white', fontSize: 16, cursor: "pointer", margin: 12
                    }} title='收起/展开' onClick={() => this.toggleCollapsed(!this.state.collapsed)}>
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
                    <AliveScope>
                        <TabMenu items={this.state.tabs}
                                 pathname={this.props.pathname}
                                 path={this.props.path}
                                 onTabRemove={this.removeTab}>
                        </TabMenu>
                    </AliveScope>
                </Content>

            </Layout>

            {!this.state.isMobileDevice && <Footer
                style={{
                    textAlign: 'center',
                    margin: 0,
                    padding: 12,
                    fontSize: "small"
                }}
            >
                {siteInfo.copyright}

                &nbsp;&nbsp;&nbsp;&nbsp;{DateUtil.now()}
            </Footer>}

        </Layout>
    }


}


