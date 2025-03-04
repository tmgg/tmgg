import React from "react";
import {SysReportChart} from "@tmgg/tmgg-base";
import {Card} from "antd";
import {withRouter} from "umi";

class ReportCode  extends React.Component {

    render() {
        const code = this.props.params.code
        return             <SysReportChart code={code} height='calc(100vh - 200px)'/>
    }
}

export  default withRouter(ReportCode)
