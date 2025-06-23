import React from "react";
import {fieldRegistry} from "./registry";
import {Alert, Input, Typography} from "antd";
import {FieldUploadImage} from "../field";

/**
 *
 * @typedef {Object} FieldComponentProps
 * @property {valueType} string 组件类型
 *
 */
class FieldValueType extends React.Component{

    render() {
        const {type, ...rest} = this.props

        switch (type){
            case 'IMG_BASE64':
                return  <FieldUploadImage maxCount={1}  {...rest}/>
            default:
                return <Input {...rest}></Input>
        }
    }
}

class ViewValueType extends React.Component{

    render() {
        const {type, value, ...rest} = this.props

        switch (type){
            case 'IMG_BASE64':
                return  <img />
            default:
                return <Typography.Text>{value}</Typography.Text>
        }
    }
}

export const ValueType = {
    FieldValueType,
    ViewValueType
}
