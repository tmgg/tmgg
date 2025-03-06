import {Badge, Dropdown} from "antd";
import {NotificationOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import React from "react";
import {history} from "umi";
import {HttpUtil, PageUtil, SysUtil} from "@tmgg/tmgg-base";
import {isMobileDevice} from "@tmgg/tmgg-commons-lang";


const ID = 'header-right';
export default class HeaderRight extends React.Component {

    state = {
        messageCount: 0,
        isMobileDevice: false
    };

    componentDidMount() {
        this.initMessage()
        document.dispatchEvent(new CustomEvent('componentDidMount', {detail: ID}))
        if(isMobileDevice()){
            this.setState({isMobileDevice:true})
        }
    }

    initMessage = () => {
        HttpUtil.get('user/msg/getMessageCount').then(rs => {
            this.setState({messageCount: rs})
        })
    }

    logout = () => {
        HttpUtil.get('/logout').finally(() => {
            localStorage.clear()
            history.replace('/login')
        })
    }

    userCenter = () => {
        history.push('/userCenter')
    }

    render() {
        const info = SysUtil.getLoginInfo()

        if(this.state.isMobileDevice){
            return <div className='header-right'>
                <a onClick={this.logout}>退出</a>
            </div>
        }

        return <div className='header-right'>

            <div className='item'>
                <UserOutlined/> {info.name}
            </div>


            <div className='item' onClick={()=>PageUtil.open('/userCenter/message','我的消息')}>
                <Badge count={this.state.messageCount } size="small">
                    <NotificationOutlined/>
                </Badge>
            </div>

            <div className='item'>

                <Dropdown menu={{
                    onClick: ({key}) => {
                        switch (key) {
                            case 'userCenter':
                                this.userCenter()
                                break;
                            case 'logout':
                                this.logout();
                                break;
                            case 'about':
                                this.about()
                                break
                        }
                    },
                    items: [
                        {key: 'userCenter', label: '个人中心'},
                        {key: 'about', label: '关于系统'},
                        {key: 'logout', label: '退出登录'},

                    ]
                }}><SettingOutlined/>

                </Dropdown>

            </div>

        </div>
    }


    about = () => {
        history.push("/about")
    }
}
