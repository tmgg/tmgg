import React from 'react';

/**
 * 属性 : url
 * 属性： value 默认选中的，会传到后台，
 * 事件: onLoadedData
 */

export interface Props {
  value?: any;
  onChange?: (value: any) => void;

  /**
   * 数据加载后触发
   * @param options
   */
  onLoadedData?: (options: any[]) => void;
}

declare class RemoteSelect extends React.Component<Props, any> {}

declare class FieldRemoteSelect extends React.Component<Props, any> {}
