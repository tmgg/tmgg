import React from 'react';
import {hasPermission} from "../system";

/**
 * 使用该组件，可以判断权限到按钮级别
 * @param {code} 权限编码
 * @props {string} code - 权限编码
 */
export class HasPerm extends React.Component {

  render() {
    let {code} = this.props;

    if (hasPermission(code)) {
      return this.props.children;
    }

  }
}
