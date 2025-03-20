import React from "react";
import {Button, Input, Modal, Row} from "antd";
import {FieldUploadFile} from "../FieldUploadFile";


export  class FieldFileBase64 extends React.Component {

    render() {
        return <>
            <Input.TextArea rows={3} value={this.props.value}></Input.TextArea>
            <FieldUploadFile url={'/utils/fileBase64'} onChange={this.onFinish}></FieldUploadFile>
        </>
    }

    onFinish = value => {
        if(this.props.onChange){
            this.props.onChange(value)
        }
    }

}
