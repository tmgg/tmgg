import React from "react";
import {fieldRegistry} from "./registry";
import {Alert, Input} from "antd";

/**
 *
 * @typedef {Object} FieldComponentProps
 * @property {valueType} string 组件类型
 *
 * @extends {React.Component<FieldComponentProps>}
 */
export  class FieldComponent extends React.Component{

    render() {
        const {valueType, ...rest} = this.props
        if(!valueType){
            return <Alert type='error' message='请设置valueType' />
        }

        const ComponentClass = fieldRegistry[valueType] || Input
        return <ComponentClass {...rest}></ComponentClass>
    }
}

