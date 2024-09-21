import React from 'react';
import {Tooltip} from 'antd';
import {StrUtil} from "@tmgg/tmgg-commons-lang";


export class Ellipsis extends React.Component {
  static defaultProps = {length: 15}

  render() {
    let {length, children} = this.props;

    let s = children;

    const ellipsis =  StrUtil.ellipsis(s, length)
    return (
      <Tooltip title={s}>{ellipsis}</Tooltip>
    );
  }
}
