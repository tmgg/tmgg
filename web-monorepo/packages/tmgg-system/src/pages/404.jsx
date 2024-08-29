import React from "react";
import {http, PageLoading, PageTool} from "@tmgg/tmgg-base";
import {Navigate} from "umi";
import {Button, Result} from "antd";

export default class extends React.Component {




    render() {

            return  <Result
                status="404"
                title={this.state.errResult.status}
                subTitle={this.state.errResult.message}
            />

    }
}
