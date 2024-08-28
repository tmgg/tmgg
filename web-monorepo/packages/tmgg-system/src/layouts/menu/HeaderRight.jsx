import {Avatar, Badge, Card, Dropdown, Menu, Modal, Spin} from "antd";
import {NotificationOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import HeaderRightMsgCard from "./HeaderRightMsgCard";
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
      history.push(SysConfig.getLoginUrl())
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
    return <>
      {sys.getSlot('TopRightButton')}
      <ul id={ID} className='header-right'>
        <li className='user'>
          <Dropdown overlay={<Card style={{padding: '16px'}}>
            <p>所属单位：{info.orgName}</p>
            <p>所属部门：{info.deptName}</p>
            <p>角色：{info.roleNames}</p>
          </Card>}>
          <span>
            <Avatar icon={<UserOutlined/>} style={{backgroundColor: theme.primaryColor}}
            ></Avatar>
            <span>&nbsp;&nbsp;{info.name}</span>
          </span>
          </Dropdown>

        </li>


        <li>
          <Dropdown overlay={<HeaderRightMsgCard/>}>
            <Badge count={this.state.messageCount}>
              <Avatar icon={<NotificationOutlined/>} style={{backgroundColor: theme.primaryColor}}/>
            </Badge>
          </Dropdown>

        </li>


        <li>
          <Dropdown overlay={<Menu>
            <Menu.Item>
              <a onClick={this.account}>个人设置</a>
            </Menu.Item>
            <Menu.Item>
              <a onClick={this.logout}>退出登录</a>
            </Menu.Item>


            {this.renderSettingMenuItemSlots()}

            <Menu.Item>
              <a onClick={this.about}>关于</a>
            </Menu.Item>
          </Menu>}>
                <span>
                    <Avatar icon={<SettingOutlined/>} style={{backgroundColor: theme.primaryColor}}></Avatar>
                </span>
          </Dropdown>

        </li>


      </ul>
    </>
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
