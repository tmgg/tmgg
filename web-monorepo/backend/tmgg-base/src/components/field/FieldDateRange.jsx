import React from "react";
import {DatePicker} from "antd";
import dayjs from "dayjs";

export class FieldDateRange extends React.Component {

    render() {
        let {value, onChange} = this.props
        // value 转换为dayjs数组
        if (value && (value.begin || value.end)) {
            const dayjsValue = [null, null];
            if (value.begin) {
                dayjsValue[0] = dayjs(value.begin)
            }
            if (value.end) {
                dayjsValue[1] = dayjs(value.end)
            }
            value = dayjsValue;
        }


        return <DatePicker.RangePicker
            value={value}
            format="YYYY-MM-DD"
            onChange={(value, dateString) => {
                console.log('Selected Time: ', value);
                console.log('Formatted Selected Time: ', dateString);
                const onChangeValue = {begin: dateString[0], end: dateString[1]}
                onChange(onChangeValue)
            }}
        >

        </DatePicker.RangePicker>
    }

}
