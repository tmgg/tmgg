/**
 * 控制按钮是否显示
 */
import { StorageUtil } from '../utils';
import {SysConfig} from "./SysConfig";

export function hasPermission(permission:any) {
  if (permission === null || permission === '') {
    return true;
  }

  const info = SysConfig.getLoginInfo();
  const { adminType, permissions } = info;

  if (adminType == 1) {
    return true;
  }

  return permissions.indexOf(permission) > -1;
}
