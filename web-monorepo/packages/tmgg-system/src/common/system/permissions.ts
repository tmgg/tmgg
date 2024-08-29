/**
 * 控制按钮是否显示
 */
import {SysConfig} from "./SysConfig";
import {arr} from "@tmgg/tmgg-base"

export function hasPermission(perm:any) {
  if (perm === null || perm === '') {
    return false;
  }

  const info = SysConfig.getLoginInfo();
  const { permissions } = info;
  if(permissions == null || permissions.length == 0){
    return false
  }

  if (arr.contains(permissions,"*")) {
    return true;
  }

  return permissions.indexOf(perm) > -1;
}
