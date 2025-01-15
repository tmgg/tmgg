import React from "react";
import {Chart, PageUtil} from "@tmgg/tmgg-base";
import {Card} from "antd";
import {history} from "umi";

export default class extends React.Component {


    render() {
        const code = PageUtil.currentPathnameLastPart()
        return <Card>
            <Chart code={code} height='calc(100vh - 200px)'/>
        </Card>
    }
}
