import React from 'react';
import {Button, Form, Image, Input, Space} from 'antd';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import "./login.less"
import {history} from 'umi';
import {HttpUtil, PageUtil, StorageUtil, SysUtil} from "@tmgg/tmgg-base";

// 登录页的自定义样式
const loginPageStyle = {}
const bg = process.env.LOGIN_BACKGROUND
console.log('登录页支持自定义背景，配置项为：process.env.LOGIN_BACKGROUND')
if(bg){
    console.log('登录背景', bg)
    loginPageStyle.backgroundImage='url('+bg+')'
}


export default class login extends React.Component {


    state = {
        // 登陆中状态
        logging: false,
    }

    componentDidMount() {
        // 内部系统登录
        let token = PageUtil.currentLocationQuery().token
        if (token) {
            token = window.location.search
            this.submit({token})
        }

    }



    submit = values => {
        this.setState({logging: true})
        HttpUtil.post('/login', values).then(token => {
            SysUtil.setToken(token)
            history.replace('/')
        }).finally(() => {
            this.setState({logging: false})
        })
    }


    render() {
        const siteInfo = SysUtil.getSiteInfo()

        return (
            <section className='login-page' style={loginPageStyle}>
                <div className="login-content">
                    <h1>{siteInfo.title}</h1>
                    <Form
                        name="normal_login"
                        className="login-form"
                        initialValues={{remember: true}}
                        onFinish={this.submit}
                        requiredMark={false}
                        colon={false}
                    >

                        <Form.Item
                            name="account"
                            rules={[{required: true, message: '请输入用户名!'}]}
                        >
                            <Input size='large' prefix={<UserOutlined className="site-form-item-icon"/>}
                                   placeholder="用户名"
                                   autoComplete="off"/>
                        </Form.Item>
                        <Form.Item
                            name="password"
                            rules={[{required: true, message: '请输入密码!'}]}
                        >
                            <Input
                                autoComplete="off"
                                prefix={<LockOutlined className="site-form-item-icon"/>}
                                type="password"
                                placeholder="密码"
                                size='large'
                            />
                        </Form.Item>


                        {siteInfo['siteInfo.captcha'] && <Form.Item name='code' label='验证码' rules={[{required: true}]}>
                            <Space>
                                <Input size='large' placeholder='验证码'/>
                                <img height={36} width={100} src={SysUtil.getServerUrl() + "captchaImage?_r="+this.state.r} onClick={()=>{
                                    this.setState({r: Math.random()})
                                }}></img>
                            </Space>
                        </Form.Item>}



                        <Form.Item style={{marginTop: 10}} label=' '>
                            <Button loading={this.state.logging} type="primary" htmlType="submit"
                                    block size='large'>
                                登录
                            </Button>
                        </Form.Item>
                    </Form>

                </div>

            </section>
        );
    }


}
