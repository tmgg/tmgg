import { Space } from 'antd';
import React, {ReactNode} from 'react';

interface Props {
  children:  ReactNode;
}

export class CenterToolbar extends React.Component<Props,any> {
  render() {
    return (
      <center>
        <Space>{this.props.children}</Space>
      </center>
    );
  }
}
