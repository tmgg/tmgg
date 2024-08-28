import React from "react";
import {theme} from "../../theme";

interface Props {
  size: 'sm' | 'md' | 'lg'
}

export class MarginY extends React.Component<Props, any> {

  static defaultProps = {
    size: 'sm'
  }

  render() {
    let size = this.getSize()

    return <div style={{marginBottom: size, marginTop: size}}></div>;
  }

  getSize() {
    switch (this.props.size) {
      case "sm":
        return theme.marginSm;
      case "md":
        return theme.marginMd
      case "lg":
        return theme.marginLg
    }
    return theme.marginSm;
  }
}
