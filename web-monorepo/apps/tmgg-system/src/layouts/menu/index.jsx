// 全局路由

import React from 'react';
import {Badge, Card, Menu, Tabs} from 'antd';
import {history, Link} from 'umi';
import "./antd_ext.less"
import "./index.less"
import '../../css/style.less'
import '../../css/table.less'
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'


import {PageLoading, ProLayout} from "@ant-design/pro-components";

import HeaderRight from "./HeaderRight";
import {HttpClient, PageTool, RouterView, showContextMenu, sys, SysConfig, TreeUtil, uid} from "../../common";
import {toggleWatermark} from "../watermark";
import hutool from "@moon-cn/hutool";


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


  };

  routerViewMap = {}

  componentDidMount() {
    this.initMenu()

    SysConfig.loadLoginData().then(() => {
      this.setState({dataLoaded: true})

    })


    PageTool.setTabRef(this)
  }


  initMenu = () => {

    HttpClient.get('appMenuTree').then(rs => {
      const list = rs.data;
      // 设置icon
      TreeUtil.every(list, (item) => {
        let IconType = Icons[ item.icon || 'SmileOutlined'];
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


  loadMenu = () => {
    return new Promise(resolve => {
      let menus = this.state.menus;
      resolve(menus)
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
    const exist = tabs.some(t => t.id == id)
    if (!exist) {
      tabs.push(item)
    }
    this.setState({tabs, selectedTabKey: id})

    toggleWatermark(item.path)
  }


  changeApp = app => {
    if (app) {
      this.setState({menus: app.children, currentAppKey: app.key}, this.actionRef.current?.reload)
    }
  }

  // 供 PageTool调用
  closeCurrentTab = (refreshParentTab) => {
    const {selectedTabKey} = this.state
    this.closeTab(selectedTabKey, refreshParentTab)
  }
  /**
   *  关闭tab
   * @param key
   * @param refreshParentTab， 是否刷新父窗口
   */
  closeTab = (key) => {
    let {tabs} = this.state
    tabs = tabs.filter(t => t.id != key)
    let selectedTabKey = tabs[tabs.length - 1]?.id
    this.setState({tabs})
    this.onTabChange(selectedTabKey)
  }


  refresh = key => {
    let {tabs} = this.state
    let tab = tabs.find(t => t.id == key)
    if (!tab) {
      return;
    }

    tab.loading = true
    this.setState({tabs}, () => {
      tab.loading = false
      this.setState({tabs})
    })
  }

  onContextMenu = (e, key) => {
    e.preventDefault()
    showContextMenu(e, ['刷新', '关闭', '关闭所有', '新窗口打开'], (name) => {
      switch (name) {
        case '刷新':
          this.refresh(key)
          break;
        case '关闭':
          this.closeTab(key)
          break
        case '关闭所有':
          this.setState({tabs: [], selectedTabKey: null})
          break
        case '新窗口打开':
          let curTab = this.state.tabs.find(t => t.id === key)
          if (curTab.iframe) {
            let path = curTab.iframePath;
            path = sys.appendTokenToUrl(path)
            window.open(path)
            return
          }
          const loc = window.location;
          let path = loc.href.repeat(loc.hash) + '/#' + curTab.path
          console.log('当前path', path)
          window.open(path)
          break

      }
    })
  }


  render() {
    console.log('开始渲染 menu index.jsx')
    if (!this.state.dataLoaded) {
      return <PageLoading/>
    }

    const title = sys.getSiteInfo().siteTitle || process.env.TITLE


    let info = sys.getLoginInfo();

    if (!info) {
      return <Card title={'未登录，请先登录'}> <Link to={'/system/login'}>登录</Link></Card>
    }
    const {list, currentAppKey} = this.state

    return <ProLayout
      contentStyle={{margin:8}}
      style={{minHeight: '100vh'}}
      headerTheme='light'
      menu={{request: this.loadMenu}}
      menuItemRender={this.menuItemRender}
      subMenuItemRender={this.menuItemRender}
      onMenuHeaderClick={() => {
        history.push("/")
      }}
      actionRef={this.actionRef}
      fixSiderbar={true}
      layout={'mix'}
      siderWidth={200}
      title={title}
      logo={logo}

      headerContentRender={() => {
        return <div style={{marginLeft: 80}}>
          <Menu mode="horizontal" selectedKeys={[currentAppKey]}>
            {
              list.map(app => <Menu.Item
                key={app.key}
                onClick={() => this.changeApp(app)}>{app.name}</Menu.Item>)
            }
          </Menu>
        </div>
      }}
      rightContentRender={() => <HeaderRight></HeaderRight>}
    >

      {this.renderTabs()}

    </ProLayout>;
  }

  // 左侧菜单项
  menuItemRender = (item, dom) => {
    return <div style={{display: 'flex', justifyContent: "start", gap: 4}}
                onClick={(event) => {
                  event.preventDefault();
                  this.openTab(item);
                  return false
                }}>

      <span>{dom}</span>
      <Badge count={item.badge} size="small">
        <span style={{display: 'inline-block', width: 10}}></span>
      </Badge>
    </div>;

  }


  renderTabs = () => {
    let {tabs} = this.state;
    if (tabs.length === 0) {
      return this.props.children;
    }

    this.routerViewMap = {}
    const items = [];
    for (const tab of tabs) {
      let {path, iframe} = tab;
      if (iframe) {
        path = tab.iframePath || tab.path
      }

      let ref = this.routerViewMap[tab.id] = React.createRef();


      let content = null
      if (iframe) {
        content =
          <iframe
            {...hutool.html.getIframeCommonProps()}
            src={sys.appendTokenToUrl(path)}
            style={{height: 'calc(100vh - 100px)'}}
            sandbox="allow-scripts allow-same-origin allow-forms"
          ></iframe>
      } else {
        content = <RouterView ref={ref} path={path} iframe={iframe} loading={tab.loading}></RouterView>;
      }


      let item = {
        forceRender: true,
        key: tab.id,
        label: <label
          title={path}
          className='cursor-pointer'
          onContextMenu={e => this.onContextMenu(e, tab.id)}
          onDoubleClick={() => this.refresh(tab.id)}>
          {tab.name}
        </label>,
        children: content
      };
      items.push(item);

    }


    return <Tabs
      className='menu-layout-tab'
      size='small'
      type='editable-card'
      hideAdd
      activeKey={this.state.selectedTabKey}
      onChange={this.onTabChange}

      onEdit={(key, action) => {
        if (action === 'remove') {
          this.closeTab(key)
        }
      }}
      animated={false}
      items={items}
    />

  }

  onTabChange = key => {
    let tab = this.state.tabs.find(tab => tab.id === key);


    toggleWatermark(tab?.path)


    console.log("onTabChange", key)
    this.setState({selectedTabKey: key}, this.triggerOnShow)
  }
  // 调用页面onShow方法
  triggerOnShow = () => {
    const key = this.state.selectedTabKey
    if (!key) {
      return
    }

    let tab = this.state.tabs.find(tab => tab.id === key);

    const routerViewRef = this.routerViewMap[tab.id]
    if (routerViewRef == null) {
      return;
    }
    let routerView = routerViewRef.current;
    if (routerView == null) {
      return;
    }

    const el = routerView.getElement();
    console.log('准备调用页面的onShow 函数', el)

    if (el.onshow === undefined) {
      console.log('onShow 函数未定义，可以在页面jsx文件中定义onShow函数，在切换当前tab会自动调用')
      return;
    }

    el.onShow(false);
  }
}


