import React from 'react';
import {Tooltip} from 'antd';
import {str} from '@tmgg/tmgg-base'
interface Props {

  /**
   *
   * @deprecated @description 显示文本长度， 中文算两个字符（中文要宽一点）
   */
  length?: number;


  children: string;
}

export class Ellipsis extends React.Component<Props, any> {
  static defaultProps = {length: 15}

  render() {
    let {length, children} = this.props;
    console.trace("裁切长度为:", length)

    let s = children;


    const ellipsis =  str.ellipsis(s, length)
    return (
      <Tooltip title={s}>{ellipsis}</Tooltip>
    );
  }
}
