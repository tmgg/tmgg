import React from 'react';
import {Modal, Tooltip, Typography} from 'antd';
import {StrUtil} from "@tmgg/tmgg-commons-lang";
import './Ellipsis.less'

export class Ellipsis extends React.Component {
  static defaultProps = {length: 15}

  render() {
    let {length, children} = this.props;
    let text = children;
    if(text.length < this.props.length){
      return text;
    }


    const shortText =  StrUtil.ellipsis(text, length)
    return (
        <span className='ellipsis-text' onClick={this.showFull}>{shortText}</span>
    );
  }

  showFull = () => {
    Modal.info({
      icon: null,
      title:'文本内容',
      content: <Typography.Text> {this.props.children}</Typography.Text>
    })

  };
}
