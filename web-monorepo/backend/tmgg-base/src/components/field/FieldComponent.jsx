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
            return <Alert type='error' message='未设置组件FieldComponent的type属性' />
        }

        const ComponentClass = fieldRegistry[type] || Input
        return <ComponentClass  {...rest}></ComponentClass>
    }
}

