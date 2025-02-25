// @ts-ignore
import React from "react";
import {ColumnsType} from "antd/es/table";
import {FormInstance} from "antd";

declare type ProTableProps = {
    columns:  ColumnsType,
    showSearch?: boolean,
    searchFormItemsRender?:(formInstance: FormInstance)=>{},

    // 选择行，通ant table
    rowSelection?: any,

    toolBarRender?: (params:any, {selectedRows:[],selectedRowKeys:[]})=>{},
    bordered?:boolean,

    /**
     * 默认每页数量
     */
    defaultPageSize?:number
};

export class ProTable extends React.Component<ProTableProps, any> {
}
