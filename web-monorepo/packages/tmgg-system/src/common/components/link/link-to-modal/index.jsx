/**
 * 像打开一个链接一样
 * 用打开modal的方式呈现一个页面
 */
import React from 'react';
import { Modal } from 'antd';
import './index.less';
import {RouterView} from "../../RouterView";

export class LinkToModal extends React.Component {
  render() {
    const { disabled, children } = this.props;
    return (
      <a onClick={this.handleClick} disabled={disabled}>
        {children}
      </a>
    );
  }

  handleClick = () => {
    const { to, width = 800, title } = this.props;
    Modal.info({
      icon: null,
      width: width,
      okText: '关闭',
      title: title || <small style={{ color: '#8C8C8C' }}>{to}</small>,
      content: (
        <section className={'link-modal'}>
          <RouterView routePath={to}></RouterView>
        </section>
      ),
    });
  };
}
