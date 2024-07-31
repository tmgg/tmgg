
export {StrTool as StrUtil} // 兼容老项目
export class StrTool {
  static equalsIgnoreCase(a:string|null, b:string|null) {
    if (a != null && b != null) {
      if (a === b) {
        return true;
      }

      if (a.toLowerCase() === b.toLowerCase()) {
        return true;
      }
    }

    return false;
  }

  static isEmpty(str:any) {
    return str == null || str.length == 0;
  }

  static isAllUpperCase(str:any) {
    if (str) {
      return str == str.toUpperCase();
    }

    return false;
  }

  static contains(str:string, str2:string) {
    return str.indexOf(str2) >= 0;
  }
  // 转驼峰
  static toUnderlineCase(name:string) {
    if (name == null) {
      return null;
    }
    let result = name.replace(/([A-Z])/g, '_$1').toLowerCase();
    if (result.startsWith('_')) {
      result = result.substring(1);
    }
    return result;
  }
}
