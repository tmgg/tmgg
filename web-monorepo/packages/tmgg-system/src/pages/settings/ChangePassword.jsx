import React from "react";
import {Button, Form, Input, Modal} from "antd";


import {history} from 'umi'
import {sys} from "../../common";
import {http} from "@tmgg/tmgg-base";

export default class extends React.Component {


  onFinish = (values) => {
    http.postForm(sys.getServerUrl() + '/sysUser/updatePwd', values).then(rs => {
      if (rs.success) {
        Modal.success({
          title: '提示',
          content: '修改密码成功',
          onOk: () => {
            localStorage.clear()
            history.push('/system/login')
          }
        })
      } else {
        Modal.error({
          title: '提示',
          content: rs.message,
        })
      }

    })
  }

  render() {
    return <div>

      <Form onFinish={this.onFinish} style={{maxWidth:400}}>
        <Form.Item name='password' label='原密码'
                   rules={[{required: true, message: '请输入原密码'}]}>
          <Input.Password type='password'></Input.Password>
        </Form.Item>
        <Form.Item name='newPassword'
                   label='新密码'
                   rules={[
                     {required: true, message: '请输入新密码'},
                     {
                       validator: (rule, value) => {
                         return new Promise((resolve, reject) => {
                           http.get(sys.getServerUrl() + "/sysUser/pwdStrength", {password: value}).then(rs => {
                             if (!rs.success) {
                               reject(rs.message)
                             }
                             resolve()
                           })
                         })

                       }
                     }
                   ]}
        >
          <Input.Password></Input.Password>
        </Form.Item>

        <Form.Item wrapperCol={{offset: 5}} style={{marginTop: 40}}>
          <Button type="primary" htmlType="submit">
            确定
          </Button>
        </Form.Item>
      </Form>

    </div>
  }
}
