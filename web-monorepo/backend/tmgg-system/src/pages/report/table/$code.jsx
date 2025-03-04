import React from "react";
import {SysReportTable} from "@tmgg/tmgg-base";
import {withRouter} from "umi";

class TableView extends React.Component {

    render() {
        const code = this.props.match.params.code
        return <SysReportTable code={code}/>
    }
}

export default withRouter(TableView)
