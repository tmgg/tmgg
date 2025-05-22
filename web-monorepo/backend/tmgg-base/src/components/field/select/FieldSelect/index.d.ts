// @ts-ignore
import React from "react";

declare type FieldSelectProps = {

    url:  string,
    value?: any
    onChange?: (value:any)=>{},

    // 数据加载完
    onLoadDataFinish?:(list:any[])=>{};

    showRefresh?:boolean;

    placeholder?:string


    disabled?:false;

    /**
     * 是否多选
     */
    multiple?:boolean;
    /**
     * 对选时返回对象数组
     *
     * str:逗号分隔
     */
    multipleMode?: 'strArr' | 'objArr'| 'str' | undefined | null
};

export class FieldSelect extends React.Component<FieldSelectProps, any> {
}
