import React from "react";


/**
 * 上下间隔
 */
export class Gap extends React.Component {

    render() {
        const {n =0} = this.props;
        let height = 16 +  8 * n

        return <div style={{height, width: '100%'}}></div>
    }
}
