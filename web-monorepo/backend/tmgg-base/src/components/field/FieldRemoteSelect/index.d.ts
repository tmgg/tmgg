import React from "react";
import {ColumnsType} from "antd/es/table";

declare type FieldRemoteSelectProps = {

    url:  string,
    value?: any
    onChange: (value:any)=>{},
    mode?: 'read'| null,

    // 数据加载完
    onLoadedData?: (list:any[])=>{},

    showRefresh:boolean;
};

export class FieldRemoteSelect extends React.Component<FieldRemoteSelectProps, any> {
}
