import React from 'react';
import { FieldRemoteMultipleSelect, FieldRemoteSelect, FieldRemoteTreeSelect } from './field';

export * from './LinkToViewFile';

export function FieldUserSelect(props:any) {
  return <FieldRemoteSelect url="/sysUser/options" {...props} />;
}

export function FieldUserMultipleSelect(props:any) {
  return <FieldRemoteMultipleSelect url="/sysUser/options" {...props} />;
}

export function FieldUnitTreeSelect(props:any) {
  return <FieldRemoteTreeSelect url="/sysOrg/tree?type=UNIT" {...props} />;
}

export function FieldOrgTreeSelect(props:any) {
  return <FieldRemoteTreeSelect url="/sysOrg/tree" {...props} />;
}

export function FieldOrgTreeMultipleSelect(props:any) {
  return <FieldRemoteMultipleSelect url="/sysOrg/tree" {...props} />;
}