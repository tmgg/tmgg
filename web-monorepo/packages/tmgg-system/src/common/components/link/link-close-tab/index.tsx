import React from 'react';
import {PageTool} from "../../../system";

export function LinkCloseTab() {
  return <a onClick={() => PageTool.close()}>关闭</a>;
}
