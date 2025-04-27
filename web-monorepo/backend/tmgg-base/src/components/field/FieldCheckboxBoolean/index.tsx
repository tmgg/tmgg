/**
 * antd 自带的checkbox 未选择时不会返回false, 和form配合时还得设置 valuePropName
 *
 *
 * 本组件改成布尔值的输入
 *
 * 选中 true， 反选 false
 *
 * 注意：null 会转为false
 *
 *
 */
import React from 'react';
import {Checkbox} from 'antd';
import {FieldProps} from "../FieldProps";


export {FieldCheckboxBoolean as CheckboxBoolean};

export class FieldCheckboxBoolean extends React.Component<FieldProps, any> {


  render() {

    let {value, mode} = this.props

    const checked = this.parseBoolean(value);

    if (mode == 'read') {
      if (checked !== null) {
        return checked ? "是" : "否"
      }
    }


    return (
      <Checkbox
        checked={checked}
        onChange={(e) => {
          if (this.props.onChange) {
            this.props.onChange(!!e.target.checked);
          }
        }}
      />
    );
  }

  parseBoolean(v: any) {
    if (v === null) {
      return undefined;
    }

    if (typeof v === 'boolean') {
      return v;
    }
    return v === 1 || v === 'true' || v === 'Y';

  }
}

