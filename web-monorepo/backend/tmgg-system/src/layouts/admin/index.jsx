// 全局路由

import React from 'react';
import {Badge, Layout, Menu, Watermark} from 'antd';

import {history, Link} from 'umi';
import "./index.less"
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons';
import defaultLogo from '../../asserts/logo.png'
import {DateUtil, isMobileDevice, theme, TreeUtil} from "@tmgg/tmgg-commons-lang";

import HeaderRight from "./HeaderRight";

import {HttpUtil, NamedIcon, PageUtil, SysUtil} from "@tmgg/tmgg-base";
import MyTabsOutlet from "./MyTabsOutlet";

const {Header, Footer, Sider, Content} = Layout;
/**
 * 带菜单的布局，主要处理布局宇框架结构
 */
export default class extends React.Component {

    state = {
        loginInfo: {},

        menus: [],
        topMenus: [],
        leftMenus: [],
        menuMap: {},


        currentTopMenuKey: null,
        currentMenuKey: null,


        collapsed: false,
        siteInfo: {},

        isMobileDevice: false,

        pathLabelMap: {}
    }


    toggleCollapsed = (v) => {
        this.setState({collapsed: v})
    }

    componentDidMount() {
        console.log('Admin Layout didMount')
        this.initMenu()
        let siteInfo = SysUtil.getSiteInfo();
        this.setState({siteInfo})

        // 判断是否手机端，自动收起菜单
        if (isMobileDevice()) {
            this.setState({collapsed: true, isMobileDevice: true})
        }

        const loginInfo = SysUtil.getLoginInfo()
        this.setState({loginInfo})
    }


    initMenu = () => {
        HttpUtil.get('menuInfo').then(info => {
            const {menus, topMenus,badgeList} = info

            let pathname = PageUtil.currentPathname();
            let currentMenuKey = null
            const menuMap = {}

            TreeUtil.traverseTree(menus, (item) => {
                item.icon = <NamedIcon name={item.icon} style={{fontSize: 12}}/>
                menuMap[item.id] = item
                if (item.path === pathname) {
                    currentMenuKey = item.id
                }
            })


            let currentTopMenuKey = null
            let leftMenus = []


            // 查早顶部菜单的当前key
            currentTopMenuKey = menus[0]?.key

            if (pathname !== "" && pathname !== "/") {
                let menu = menuMap[currentMenuKey]
                currentTopMenuKey = menu?.rootid
            }

            leftMenus = menuMap[currentTopMenuKey]?.children

            this.setState({menus, menuMap, topMenus, leftMenus, currentTopMenuKey, currentMenuKey})

            this.storePathLabel(Object.values(menuMap))

            this.loadBadge(badgeList)
        })


    }
    actionRef = React.createRef()


    loadBadge = list => {
        for(let item of list){
            const {menuId, url} = item
            HttpUtil.get(url,null, false).then(rs=>{
                const {leftMenus} = this.state
                const menu = TreeUtil.findByKey(menuId,leftMenus)
                if(menu){
                    menu.icon = <Badge dot count={rs} size={"small"}>{menu.icon}</Badge>
                    this.setState({leftMenus:[...leftMenus]})
                }

            })
        }
    };

    storePathLabel(menus) {
        const map = {}
        for (let menu of menus) {
            const {label, path} = menu;
            map[path] = label;
        }
        this.setState({pathLabelMap: map})
    }


    render() {
        let logo = this.props.logo || defaultLogo
        const {siteInfo, topMenus, loginInfo,leftMenus} = this.state


        return <Layout className='main-layout'>
            <Header className='header'>
                <div className='header-left'>

                    <img className='logo-img' src={logo} onClick={() => history.push('/')}/>
                    <h3 className='hide-on-mobile'>
                        <Link to="/" style={{color: theme["primary-color"]}}>{siteInfo.title} </Link>
                    </h3>


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
                          style={{lineHeight: '42px',  borderBottom: 'none', backgroundColor: '#f5f5f5'}}
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

                    <Menu items={leftMenus}
                          theme='dark'
                          mode="inline"
                          className='left-menu'
                          onClick={({key, item}) => {
                              let {path, id} = item.props;
                              this.setState({currentMenuKey: id})
                              history.push(path)
                          }}
                          selectedKeys={[this.state.currentMenuKey]}
                    >
                    </Menu>

                </Sider>

                <Content id='admin-layout-content'>
                    {this.getContent(loginInfo)}
                </Content>

            </Layout>

            <Footer
                className='hide-on-mobile'
                style={{
                    textAlign: 'center',
                    margin: 0,
                    padding: 12,
                    fontSize: "small"
                }}
            >
                {siteInfo.copyright}

                &nbsp;&nbsp;&nbsp;&nbsp;当前时间 <time title='当前时间'>{DateUtil.now()}</time>
            </Footer>

        </Layout>
    }


    getContent = () => {
        const {siteInfo, loginInfo} = this.state
        if (this.state.menus.length === 0) { // 加载菜单中
            return
        }
        if (siteInfo.waterMark === true) {
            return <Watermark content={[loginInfo.name,  loginInfo.account ]}>
                <MyTabsOutlet pathLabelMap={this.state.pathLabelMap}/>
            </Watermark>
        }


        return <MyTabsOutlet pathLabelMap={this.state.pathLabelMap}/>

    };
}


