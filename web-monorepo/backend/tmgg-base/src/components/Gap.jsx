import React from "react";


/**
 * 上下间隔
 */
export class Gap extends React.Component {

    render() {
        const {height = 16} = this.props;
        return <div style={{height, width: '100%'}}></div>
    }
}
