import React, {ReactNode} from 'react';

import { Col, Row } from 'antd';


interface LeftRightLayoutProps {
  leftSize: number;
  children:ReactNode[];
}

const style = {
  marginRight: 0,
};



export class LeftRightLayout extends React.Component<LeftRightLayoutProps,any> {
  static defaultProps = {
    leftSize: 300
  }
  render() {
    const {leftSize, children} = this.props;
    return (
      <Row gutter={4} wrap={false} style={style}>
        <Col flex={leftSize + 'px'}>
          {children[0]}
        </Col>
        <Col flex="auto">{children[1]}</Col>
      </Row>
    );
  }
}
