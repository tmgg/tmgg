import React from 'react';
import { Radio } from 'antd';
import {FieldProps} from "../../FieldProps";



export class FieldRadioBoolean extends React.Component<FieldProps, any> {
  render() {
    return (
      <Radio.Group value={this.props.value} onChange={this.props.onChange}>
        <Radio value={true}>是</Radio>
        <Radio value={false}>否</Radio>
      </Radio.Group>
    );
  }
}
