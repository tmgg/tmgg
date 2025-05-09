// @ts-ignore
import React from "react";

declare type FieldTableSelectProps = {
    /**
     * 远程方法连接， 返回的data数据为 io.tmgg.lang.obj.Table
     */
    url:string;

    type: 'checkbox' | 'radio';


    /**
     * 值的字段， 默认为 id、value
     */
    valueDataIndex?:string;
    /**
     * 显示的字段, 默认为 name、label、title
     */
    labelDataIndex?: string;

    placeholder?:string;

};

/**
 * 下拉表格
 */
export class FieldTableSelect extends React.Component<FieldTableSelectProps, any> {
}
