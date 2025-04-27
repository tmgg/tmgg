import React from "react";
import {fieldRegistry} from "./registry";
import {Alert, Input} from "antd";

/**
 *
 * @typedef {Object} FieldComponentProps
 * @property {valueType} string 组件类型
 *
 */
export  class FieldComponent extends React.Component{

    render() {
        const {type, ...rest} = this.props
        if(!type){
            return <Input {...rest}></Input>
        }

        const ComponentClass = fieldRegistry[type] || Input
        return <ComponentClass  {...rest}></ComponentClass>
    }
}

