/**
 * 点击查看文件
 */
import React, {ReactNode} from 'react';
import {Modal} from 'antd';
import {SysConfig} from "../../system";

interface Props {

  /***
   * 文件id
   */
  value: string;


  children:ReactNode
}

export class LinkToViewFile extends React.Component<Props, any> {
  showFile = async () => {
    let {value} = this.props;
    let url = SysConfig.getServerUrl() + 'sysFile/preview/' + value;
    url = SysConfig.appendTokenToUrl(url);

    Modal.info({
      icon: undefined,
      title: '文件预览',
      width: 960,
      okText: '关闭',
      closable: true,
      content: (
        <iframe
          src={url}
          frameBorder={0}
          width="100%"
          height="100%"
          style={{minHeight: 700}}
        ></iframe>
      ),
    });
  };

  render() {
    return <a onClick={this.showFile}>{this.props.children}</a>;
  }
}
