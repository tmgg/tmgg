import React from 'react';
import {hasPermission} from "../system";

/**
 * 使用该组件，可以判断权限
 * @param
 */
export class HasPerm extends React.Component {

  render() {
    let {code} = this.props;

    hasPermission()
    if (hasPermission(code)) {
      return this.props.children;
    }

  }
}
