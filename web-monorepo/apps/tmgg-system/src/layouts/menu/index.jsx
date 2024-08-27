// 全局路由

import React from 'react';
import {Badge, Card, Dropdown, Menu, Segmented, Tabs} from 'antd';
import {history, Link, Outlet} from 'umi';
import "./antd_ext.less"
import "./index.less"
import '../../css/style.less'
import '../../css/table.less'
import * as Icons from '@ant-design/icons';
import logo from '../../asserts/logo.png'

import {PageLoading, ProLayout} from "@ant-design/pro-components";

import HeaderRight from "./HeaderRight";
import {HttpClient, showContextMenu, sys, SysConfig, TreeUtil, uid} from "../../common";
import hutool from "@moon-cn/hutool";
import {PageTool} from "@tmgg/tmgg-base";
import {CloseOutlined} from "@ant-design/icons";


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
          let path = loc.href.repeat(loc.hash) + '/' + curTab.path
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

      <Outlet />

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

    const items = [];
    for (const tab of tabs) {
      let {path, iframe} = tab;
      if (iframe) {
        path = tab.iframePath || tab.path
      }



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
      }




      let item = {
        icon: tab.icon,
        key: tab.id,
        label: tab.label,
        value: tab.path,
      };
      items.push(item);
    }

    const items2 = [
      {
        label: '1st menu item',
        key: '1',
      },
      {
        label: '2nd menu item',
        key: '2',
      },
      {
        label: '3rd menu item',
        key: '3',
      },
    ];
    return < >
      <Dropdown
          menu={{
            items:items2
          }}
          trigger={['contextMenu']}
      >
        <div
            style={{
              height: 200,
              textAlign: 'center',
              lineHeight: '200px',
            }}
        >
          Right Click on here
        </div>
      </Dropdown>
      <Menu
          items={items}
          mode="horizontal"
          onChange={(value) => {
            PageTool.open(value)
            console.log(value); // string
          }}
      />
    </>
  }

  onTabChange = (key,item) => {
    console.log("onTabChange", key,item)

    this.setState({selectedTabKey: key}, this.triggerOnShow)

    const {tabs} = this.state;
    const tab = tabs.find(t=>t.id=== key)
    PageTool.open(tab.path)

  }

}


