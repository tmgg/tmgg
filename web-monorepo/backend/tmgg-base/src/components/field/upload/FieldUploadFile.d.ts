// @ts-ignore
import React from "react";
import {ColumnsType} from "antd/es/table";
import {FormInstance} from "antd";

declare type FieldUploadFileProps = {
   value?: any;
    onChange?: ()=>{},

    /**
     * 自定义上传路径
     */
    url?: string
};

export class FieldUploadFile extends React.Component<FieldUploadFileProps, any> {
}
