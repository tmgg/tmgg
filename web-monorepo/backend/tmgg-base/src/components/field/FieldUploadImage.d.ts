// @ts-ignore
import React from "react";
import {ColumnsType} from "antd/es/table";
import {FormInstance} from "antd";

declare type FieldUploadImageProps = {
    /**
     * 是否支持裁切
     */
    crop?: boolean,
     maxNum?:Number;
     multiple?:boolean;
};

export class FieldUploadImage extends React.Component<FieldUploadImageProps, any> {
}
