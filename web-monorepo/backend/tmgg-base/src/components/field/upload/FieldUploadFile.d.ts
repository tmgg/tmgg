// @ts-ignore
import React from "react";
// @ts-ignore
import {CropperProps} from "react-easy-crop/Cropper";


declare type FieldUploadFileProps = {
    value?: string; // 文件的值，sysFile的id, 逗号分割
    onChange?: (v:string)=>{};
    onFileChange?:(fileList:any[])=>{}

    /**
     * 是否裁切图片
     */
    cropImage?:boolean;
    cropperProps?: CropperProps;

    maxCount?: number;

    accept?: string;

    children?: React.ReactNode;
};

/**
 * 上传图片前裁切， 单张图片
 *
 * 可参考 react-easy-crop
 */
export class FieldUploadFile extends React.Component<FieldUploadFileProps, any> {

}
