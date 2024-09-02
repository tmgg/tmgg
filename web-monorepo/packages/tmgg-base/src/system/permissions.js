/**
 * 控制按钮是否显示
 */
import {SysConfig} from "./SysConfig";
import {arr} from "@tmgg/tmgg-base"

/**
 * 是否拥有权限
 * @param perm 权限码
 */
export function hasPermission(perm) {
  if (perm === null || perm === '') {
    return false;
  }

  const info = SysConfig.getLoginInfo();
  const { permissions } = info;
  if(permissions == null || permissions.length === 0){
    return false
  }

  if (arr.contains(permissions,"*")) {
    return true;
  }

  return permissions.indexOf(perm) > -1;
}
