import React from "react";
import {Button, Modal} from "antd";
import SliderCaptcha from "rc-slider-captcha";
import {HttpClient} from "../../../system";
import {FieldProps} from "../FieldProps";
import {uid} from "../../../utils";
import {CheckCircleOutlined, InfoCircleOutlined, RightCircleFilled, RightOutlined} from "@ant-design/icons";

export class FieldCaptcha extends React.Component<FieldProps, any> {

  state = {
    captchaOk: false,
    openCaptcha: false
  }

  clientId = 'id-' + uid()

  render() {
    if (this.state.captchaOk) {
      return <Button icon={<CheckCircleOutlined/>}>验证成功</Button>
    }


    return <>
      <Button onClick={this.showCaptcha}
              icon={<InfoCircleOutlined/>}>{this.state.captchaOk ? "验证成功" : '点击按钮进行验证'}</Button>
      <Modal destroyOnClose open={this.state.openCaptcha} footer={null} title={null} width={670}  closable={false}>
        {this.renderCaptcha()}
      </Modal>
    </>
  }

  showCaptcha = () => {
    this.setState({openCaptcha: true})
  }
  renderCaptcha = () => <SliderCaptcha
    bgSize={{
      width: 640,
      height:360
    }}
    request={() => {
      return new Promise(resolve => {
        HttpClient.get("captcha/get", {clientId: this.clientId}).then(rs => {
          resolve(rs.data)
        })
      })
    }}
    onVerify={(param) => {
      return new Promise((resolve, reject) => {
        let clientId = this.clientId;
        HttpClient.post("captcha/verify", {...param, clientId}).then(rs => {
          if (rs.data) {
            this.setState({captchaOk: true, openCaptcha: false})
            this.props.onChange(clientId)
            resolve(true)
          } else {
            this.props.onChange(false)
            reject()
          }
        })
      })

    }}
  />;
}
