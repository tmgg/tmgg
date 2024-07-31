import React from 'react';

import moment from 'moment';
import { TimePicker } from 'antd';
import {valueToDate} from "./TimePickerTool";

/**
 * 时分
 *
 * 值类型为字符串
 *
 *
 */
export { HHmmPickerString as FieldHHmmPickerString };
const FMT = 'HH:mm';

export class HHmmPickerString extends React.Component {

  static FORMAT = FMT

  onChange = (date) => {
    const str = date ? date.format(FMT) : null;
    this.props.onChange(str);
  };

  render() {
    let { value, readOnly = false, mode } = this.props;
    if (mode == 'read') {
      return value;
    }
    value = valueToDate(value,FMT)


    return (
      <TimePicker
        {...this.props}
        value={value}
        onChange={this.onChange}
        format={FMT}
        disabled={readOnly || this.props.disabled}
      ></TimePicker>
    );
  }
}
