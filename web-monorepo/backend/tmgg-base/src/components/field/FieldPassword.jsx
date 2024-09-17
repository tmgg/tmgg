import React from "react";
import {Checkbox, Input, Space} from "antd";
import {EyeInvisibleOutlined, EyeOutlined} from "@ant-design/icons";

export  class FieldPassword extends React.Component {
    state = {
        visible: false
    }

    render() {
        let v = this.props.value;
        if (this.props.mode === 'read') {
            if(v == null){
                return null
            }
            let visible = this.state.visible;
            return <Space>
                <span >{this.state.visible ? v : '******'}</span>
                <a onClick={() => this.setState({visible: !visible})}>
                    {visible ? <EyeOutlined /> : <EyeInvisibleOutlined />}
                </a>
            </Space>
        }

        return (
            <Input.Password value={v} onChange={this.props.onChange} {...this.props.fieldProps} />
        )
    }

}
