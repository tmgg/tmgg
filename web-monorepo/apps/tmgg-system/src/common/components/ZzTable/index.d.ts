import React, {RefObject} from 'react';

declare interface ZzTableProps {

  requestUrl?: string;
  columns: any[],
  ref: RefObject<any>,
  formRef: RefObject<any>,
  rowSelection?: boolean,
  rowKey?: string;

  /**
   * 是否显示搜索
   */
  search?:boolean;

  toolBarRender?:(action:any, params:{selectedRows:any[]})=>any

  type?: string
}
declare class ZzTable extends React.Component<ZzTableProps, any> {}
