import React from "react";
import {Button, Flex, Form, Input, InputNumber, Spin} from "antd";
import {
    FieldDateTimePickerString,
    FieldDictSelect,
    FieldSelect,
    HttpUtil
} from "@tmgg/tmgg-base";
import {UrlUtil} from "@tmgg/tmgg-commons-lang";
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";


export default class extends React.Component {

    render() {
        return <div>
            demo表单
            {JSON.stringify(this.props)}
        </div>
    }
}
