import React from "react";
import {FieldRemoteSelect} from "./FieldRemoteSelect";
import {FieldDictRadio} from "./dict";
import {FieldPassword} from "./FieldPassword";
import {Input} from "antd";
import {FieldDateTimePickerString} from "./time";


export const fieldRegistry = {
    'remoteSelect': FieldRemoteSelect,
    'dict':FieldDictRadio,
    'dictRadio':FieldDictRadio,
    'password':FieldPassword,
    'datetime':FieldDateTimePickerString
}




/**
 *
 * @typedef {Object} FieldComponentProps
 * @property {componentType} string 组件类型
 * @property {componentProps} string 组件属性
 *
 * @extends {React.Component<FieldComponentProps>}
 */
export  class FieldComponent extends React.Component{

    render() {
        const {componentType,componentProps = {}, ...rest} = this.props

        const ComponentClass =fieldRegistry[componentType] || Input
        return <ComponentClass {...componentProps} {...rest}></ComponentClass>
    }
}

