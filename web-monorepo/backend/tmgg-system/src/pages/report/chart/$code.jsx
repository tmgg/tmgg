import React from "react";
import {SysReportChart} from "@tmgg/tmgg-base";
import {Card} from "antd";
import {withRouter} from "umi";

class ChartView extends React.Component {

    render() {
        const code = this.props.match.params.code
        return <Card>
            <SysReportChart code={code} height='calc(100vh - 200px)'/>
        </Card>
    }
}

export default withRouter(ChartView)
