import React from 'react';
import { Button } from 'antd';
import {PageTool} from "../../../system";

export function LinkToTab(props: { to: any; title: any; children: any; disabled: any }) {
  const { to, title, children, disabled } = props;

  return (
    <Button
      type="link"
      onClick={() => PageTool.open(title || children, to)}
      disabled={disabled}
    >
      {' '}
      {children}{' '}
    </Button>
  );
}
