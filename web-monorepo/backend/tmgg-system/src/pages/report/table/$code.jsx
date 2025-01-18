import React from "react";
import {SysReportTable} from "@tmgg/tmgg-base";
import {Card} from "antd";
import {withRouter} from "umi";

class TableView extends React.Component {

    render() {
        const code = this.props.match.params.code
        return <Card>
            <SysReportTable code={code} />
        </Card>
    }
}

export default withRouter(TableView)
