import {Avatar, Badge, Dropdown, Menu, Modal, Popover, Spin} from "antd";
import {NotificationOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import React from "react";
import {history} from "umi";
import {HttpClient, PageTool, sys, SysConfig} from "../../common";
import ReactMarkdown from "react-markdown";
import {theme} from "../../common";

const ID = 'header-right';
export default class HeaderRight extends React.Component {
  state = {
    info: null,
    messageCount: 0,

    openSwitchUnitModal: false
  };

  componentDidMount() {
    let info = sys.getLoginInfo()
    if (info == null) {
      history.push(sys.getLoginUrl())
      return
    }

    this.initMessage()
    this.setState({info})

    document.dispatchEvent(new CustomEvent('componentDidMount', {detail: ID}))
  }

  initMessage = () => {
    HttpClient.get('/getMessageCount').then(rs => {
      this.setState({messageCount: rs.data})
    })
  }

  logout = () => {
    HttpClient.get('/logout').finally(() => {
      localStorage.clear()
      history.replace(SysConfig.getLoginUrl())
    })
  }

  account = () => {
    PageTool.open('个人设置','/settings')
  }

  render() {
    const {info} = this.state
    if (info == null) {
      return <div>
        <Spin/>
      </div>
    }
    return <div className='header-right'>

      <Popover className='item' title={info.name || '姓名未定义'} content={<div style={{width:200}}>
        <p>{info.roleNames}</p>
        <p>{info.orgName}</p>
        <p>{info.deptName}</p>
      </div>}>
        <UserOutlined/>
      </Popover>


        <div className='item'>
          <Badge count={this.state.messageCount || 1} size="small">
            <NotificationOutlined/>
          </Badge>
        </div>

      <div className='item'>

          <Dropdown menu={{
            onClick:(key)=>{
              switch (key) {
                case 'settings':
                  this.account()
                  break;
                case 'logout':
                  this.logout();
                  break;
                case 'about':
                  this.about()
                      break
              }
            },
            items:[{
            key:'settings',label:'个人设置'
                }, {key:'logout',label:'退出登录'},
                  {key:'about',label:'关于'}
                ]}}><SettingOutlined/>

          </Dropdown>

      </div>

    </div>
  }


  about = () => {
    HttpClient.get('sysAbout').then(rs => {
      Modal.info({
        title: '关于',
        icon: null,
        width: 600,
        content: <ReactMarkdown>{rs.data}</ReactMarkdown>
      })
    })
  }

  renderSettingMenuItemSlots = () => {
    let node = sys.getSlot('TopRightSettingMenuItem');
    if(node){
      return <Menu.Item >          {node}        </Menu.Item>
    }
  }
}