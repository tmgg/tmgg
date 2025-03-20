import React from "react";
import {Card, Form, Select} from "antd";
import {FieldEditor, FieldEditTable, FieldRemoteSelect} from "@tmgg/tmgg-base";
import FieldFileBase64 from "@tmgg/tmgg-base/src/components/field/FieldFileBase64";

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
