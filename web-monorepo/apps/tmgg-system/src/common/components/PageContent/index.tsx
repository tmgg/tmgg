import React, {ReactNode} from 'react';
import {theme} from "../../theme";

interface Props {
  bgGray?: boolean;

  children:ReactNode
}


export class PageContent extends React.Component<Props, any> {
  render() {
    let { children, bgGray = true} = this.props;
    let style = {
      background: bgGray ? theme.backgroundColorBase : 'white',
      padding: 12,
      minHeight: 500
    };

    return (
      <div style={style}>
        {children}
      </div>
    );
  }
}
