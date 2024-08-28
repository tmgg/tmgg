import React from 'react';
import {Button, Form, Input, message} from 'antd';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import "./login.less"
import {history} from 'umi';
import {FieldCaptcha, HttpClient, sys, SysConfig} from "../common";
import {PageTool} from "@tmgg/tmgg-base";


export default class login extends React.Component {


  state = {
    // 登陆中状态
    logining: false,

    siteInfo: {
      siteTitle: null,
      copyright: null,
      captchaEnable: null
    }

  }

  componentDidMount() {
    // 内部系统登录
    let token = PageTool.currentLocationQuery().token
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
    localStorage.clear()

    this.setState({logining: true})
    HttpClient.post('/login', values).then(rs => {
      message.info(rs.message)
      localStorage.setItem(sys.AUTH_TOKEN_NAME, rs.data)

      const hide = message.loading("加载初始数据中")
      SysConfig.loadLoginData().then(() => {
        console.log("跳转到首页....")
        hide();
        history.replace('/')
      })
    }).finally(() => {
      this.setState({logining: false})
    })
  }


  render() {
    let siteInfo = SysConfig.getSiteInfo()


    return (
      <section className='login-page'>

        <div className="login-content">
          <h1>{siteInfo.siteTitle}</h1>
          <Form
            name="normal_login"
            className="login-form"
            initialValues={{remember: true}}
            onFinish={this.submit}
          >

            <Form.Item
              name="account"
              rules={[{required: true, message: '请输入用户名!'}]}
            >
              <Input size='large' prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="用户名"
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


            {siteInfo.captchaEnable &&
              <Form.Item name='clientId' required rules={[{required: true, message: '请先进行校验'}]}>
                <FieldCaptcha></FieldCaptcha>
              </Form.Item>}


            <Form.Item style={{marginTop: 10}}>
              <Button loading={this.state.logining} type="primary" htmlType="submit"
                     block size='large'>
                登录
              </Button>
              {sys.isSsoLoginEnable() && <Button type='link' onClick={()=>history.push('/system/ssoLogin')} style={{marginTop:16}}>扫码登录</Button>}
              {sys.getSlot('LOGIN_PAGE_FORM_BUTTON_NEXT')}
            </Form.Item>
          </Form>


        </div>

        <footer style={{position: "fixed", bottom: 30, left: 0, right: 0, color: "white", fontSize: 'small', fontWeight:'lighter'}}>
          <center>
            {siteInfo.copyright}
          </center>
        </footer>
      </section>
    );
  }


}
