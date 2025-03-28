import React from "react";
import {Card} from "antd";
import {FieldFileBase64} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        info: {}
    }

    render() {

        return <Card title='测试页面'>

            <FieldFileBase64 onChange={v=>console.log(v)} />


        </Card>
    }
}
