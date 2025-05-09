// @ts-ignore
import React from "react";

declare type FieldDropdownTableProps = {
    /**
     * 远程方法连接， 返回的data数据为 io.tmgg.lang.obj.Table
     */
    url:string;

    type: 'checkbox' | 'radio';

};

/**
 * 下拉表格
 */
export class FieldDropdownTable extends React.Component<FieldDropdownTableProps, any> {
}
