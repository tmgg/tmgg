import React from "react";
import {Button, Input, Modal, Row} from "antd";
import {FieldUploadFile} from "../FieldUploadFile";


export default class FieldFileBase64 extends React.Component {

    render() {
        return <>
            <div style={{display: 'flex', justifyContent: 'space-between',gap:12}}>
                <Input.TextArea rows={3}></Input.TextArea>
                <FieldUploadFile url={'/utils/fileBase64'} onChange={this.onFinish}></FieldUploadFile>
            </div>
        </>
    }

    onFinish = v => {
        if(this.props.onChange){
            this.props.onChange(v)
        }
    };

}
