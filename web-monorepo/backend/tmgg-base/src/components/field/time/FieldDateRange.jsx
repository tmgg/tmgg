import React from "react";
import {DatePicker} from "antd";
import dayjs from "dayjs";

/**
 * 日期区间选择， 格式为时间戳数组
 */
export class FieldDateRange extends React.Component {

    render() {
        let {value, onChange} = this.props

       if(value && value.length === 2){
          value = [dayjs(value[0]),dayjs(value[1])]
         }

        return <DatePicker.RangePicker
            value={value}
            format={"YYYY-MM-DD"}
            onChange={(value, dateString) => {
                onChange([value[0].valueOf(),value[1].valueOf()])
            }}
        >
        </DatePicker.RangePicker>
    }

}
