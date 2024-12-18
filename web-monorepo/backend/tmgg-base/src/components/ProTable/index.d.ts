// @ts-ignore
import React from "react";
import {ColumnsType} from "antd/es/table";
import {FormInstance} from "antd";

declare type ProTableProps = {
    columns:  ColumnsType,
    showSearch?: boolean,
    searchFormItemsRender?:(formInstance: FormInstance)=>{},
};

export class ProTable extends React.Component<ProTableProps, any> {
}
