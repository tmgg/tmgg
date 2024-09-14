import react from 'react'
import {Input} from '@tarojs/components'
import {
  Form,
  FormItem,
  Icon,
  Switch,
  RadioGroup,
  Radio,
  Button,
  Dialog,
} from '@antmjs/vantui'
import React from "react";
import {HttpUtil} from "@tmgg/tmgg-app-base";
import Taro from "@tarojs/taro";
import {SysUtil} from "@tmgg/tmgg-commons-lang";

export default class Demo extends react.Component {

  state = {
    logining:false
  }


  submit = (errs, values) => {
    this.setState({logining: true})
    SysUtil.setLoginStatus(0)
    HttpUtil.post('/login', values).then(token => {
      SysUtil.setLoginStatus(1)
      SysUtil.setToken(token)
      Taro.redirectTo({url:"/pages/main/index/index"})
    }).finally(() => {
      this.setState({logining: false})
    })
  }

  formRef = React.createRef()

  render() {
    return (
      <>
        <Form
          ref={this.formRef}
          onFinish={this.submit}
          initialValues={{account:'superAdmin', password:"qetu.1357"}}
        >
          <FormItem
            label="用户名"
            name="account"
            required
            valueFormat={(e) => e.detail.value}
          >
            <Input placeholder="请输入用户名"/>
          </FormItem>

          <FormItem
            label="密码"
            name="password"
            required
            valueFormat={(e) => e.detail.value}
          >
            <Input password placeholder="请输入密码"/>
          </FormItem>


          <Button type="primary" formType="submit" onClick={()=>this.formRef.current.submit()} >
            登录
          </Button>
        </Form>
      </>
    )
  }
}

