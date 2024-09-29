import React from 'react';
import {Button, Form, Input, Space} from 'antd';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import "./login.less"
import {history} from 'umi';
import {HttpUtil, PageUtil, StorageUtil, SysUtil} from "@tmgg/tmgg-base";


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

        this.fixedBugBackgroundImage();
    }

    /**
     * 如果讲前端页面部署在非根目录下，请求背景图片使用相对位置
     * 为什么不在less中直接指定？ 因为编译不通过
     */
    fixedBugBackgroundImage() {
        let el = document.createElement('style');
        el.innerText = ".login-page {" +
            "background-image: url('./login_bg.jpg')" +
            "}"
        document.head.append(el)
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
            <section className='login-page'>

                <div className="login-content">
                    <h1>{siteInfo.title}</h1>
                    <Form
                        name="normal_login"
                        className="login-form"
                        initialValues={{remember: true}}
                        onFinish={this.submit}
                        labelCol={{flex: '80px'}}
                        requiredMark={false}
                        colon={false}
                    >

                        <Form.Item
                            label='账号'
                            name="account"
                            rules={[{required: true, message: '请输入用户名!'}]}
                        >
                            <Input size='large' prefix={<UserOutlined className="site-form-item-icon"/>}
                                   placeholder="用户名"
                                   autoComplete="off"/>
                        </Form.Item>
                        <Form.Item
                            label='密码'
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

                        <Form.Item name='captcha' label='验证码' rules={[{required: true}]}>
                            <Space>
                                <Input size='large'/>
                                <img height={'38px'} src={SysUtil.getServerUrl() + "captchaImage"}></img>
                            </Space>
                        </Form.Item>


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
