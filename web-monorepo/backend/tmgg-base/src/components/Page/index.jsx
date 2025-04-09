import React from "react";
import './index.less'

/**
 * 上下间隔
 */
export class Page extends React.Component {
    static defaultProps = {

    }

    render() {
        return <div className={'tmgg-page'} >
            {this.props.children}
        </div>
    }


}
