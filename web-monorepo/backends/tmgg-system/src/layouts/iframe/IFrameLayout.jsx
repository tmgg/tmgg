import React from "react";

/**
 * 嵌入iframe的布局，不带菜单
 */
import './IFrameLayout.less'

export default function (props) {
  return <div className={'z-layout-iframe'}>{props.children}</div>
}
