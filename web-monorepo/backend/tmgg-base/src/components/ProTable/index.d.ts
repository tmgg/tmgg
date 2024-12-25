// @ts-ignore
import React from "react";
import {ColumnsType} from "antd/es/table";
import {FormInstance} from "antd";

declare type ProTableProps = {
    columns:  ColumnsType,
    showSearch?: boolean,
    searchFormItemsRender?:(formInstance: FormInstance)=>{},
    // 总结兰
    summary: (pageData:any)=>{}
};

export class ProTable extends React.Component<ProTableProps, any> {
}
