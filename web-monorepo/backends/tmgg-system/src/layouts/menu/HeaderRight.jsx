import {Badge, Dropdown} from "antd";
import {NotificationOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import React from "react";
import {history} from "umi";
import {HttpUtil, SysUtil} from "@tmgg/tmgg-base";


const ID = 'header-right';
export default class HeaderRight extends React.Component {

    state = {
        messageCount: 0,
    };

    componentDidMount() {
        this.initMessage()

        document.dispatchEvent(new CustomEvent('componentDidMount', {detail: ID}))
    }

    initMessage = () => {
        HttpUtil.get('/getMessageCount').then(rs => {
            this.setState({messageCount: rs})
        })
    }

    logout = () => {
        HttpUtil.get('/logout').finally(() => {
            localStorage.clear()
            history.replace('/login')
        })
    }

    account = () => {
        history.push('/settings')
    }

    render() {
        const info = SysUtil.getLoginInfo()

        return <div className='header-right'>

            <div className='item'>
                <UserOutlined/> {info.name}
            </div>


            <div className='item'>
                <Badge count={this.state.messageCount || 1} size="small">
                    <NotificationOutlined/>
                </Badge>
            </div>

            <div className='item'>

                <Dropdown menu={{
                    onClick: ({key}) => {
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
                    items: [
                        {key: 'settings', label: '个人设置'},
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