import React, {ReactNode} from 'react';
import {SysConfig} from "../../system";

/**
 * 使用该标签，可以判断权限到按钮级别,
 */

interface HasPermProps {
  /**
   * @description 权限代码， 如user:add
   */
  code: string;

  children: ReactNode
}

export class HasPerm extends React.Component<HasPermProps, any> {
  render() {
    let {code} = this.props;
    let perms = SysConfig.getPermissions();
    let ok = perms != null && perms.indexOf('*') != -1 || perms.indexOf(code) != -1;
    if (ok) {
      return this.props.children;
    }

  }
}
