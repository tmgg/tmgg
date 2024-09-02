import React from 'react';
import {Tooltip} from 'antd';
import {str} from '@tmgg/tmgg-base'


export class Ellipsis extends React.Component {
  static defaultProps = {length: 15}

  render() {
    let {length, children} = this.props;

    let s = children;

    const ellipsis =  str.ellipsis(s, length)
    return (
      <Tooltip title={s}>{ellipsis}</Tooltip>
    );
  }
}
