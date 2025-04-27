import React from 'react';
import {Radio} from 'antd';


export class FieldRadioBoolean extends React.Component {
    render() {
        let {mode, value, onChange} = this.props;


        if(value != null){
            // 转换一下，以免一些字符串格式出现
            if(value === 'true'){
                value = true
            }else if(value === 'false'){
                value = false
            }
        }


        if (mode === 'read') {
            if (value == null) {
                return
            }




            return value ? '是' : '否'
        }

        return (
            <Radio.Group value={value} onChange={onChange}>
                <Radio value={true}>是</Radio>
                <Radio value={false}>否</Radio>
            </Radio.Group>
        );
    }
}
