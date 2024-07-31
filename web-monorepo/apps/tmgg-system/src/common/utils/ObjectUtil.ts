export class ObjectUtil {
  static isString(obj: any) {
    return obj != null && typeof obj == 'string';
  }

  static removeKeys(obj: any, ...keys: any[]) {
    for (let key of keys) {
      delete obj[key];
    }
  }

  static containsKey(obj: object, searchKey: string) {
    let keys = Object.keys(obj);

    return keys.indexOf(searchKey) >= 0;
  }

  static containsAnyKey(obj: object, ...searchKeys: any[]) {
    let keys = Object.keys(obj);

    for (let i = 0; i < searchKeys.length; i++) {
      const searchKey = searchKeys[i];
      if (keys.indexOf(searchKey) >= 0) {
        return true;
      }
    }

    return false;
  }

  static allHasValue(obj: any, ...keys: any[]) {
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      if (obj[key] == null) {
        return true;
      }
    }
    return true;
  }

  static removeNullValue(obj: any) {
    for (let k in obj) {
      if (obj[k] == null) {
        delete obj[k];
      }
    }
    console.log('remove result', obj);
  }

  // 比较两个对象的相等，通过怕
  static propEquals(o1: any, o2: any) {
    if (o1 == null || o2 == null) {
      return false;
    }
    let keys1 = Object.keys(o1);
    let keys2 = Object.keys(o2);
    if (keys1.length != keys2.length) {
      return false;
    }

    for (let key of keys1) {
      if (o1[key] != o2[key]) {
        return false;
      }
    }

    for (let key of keys2) {
      if (o1[key] != o2[key]) {
        return false;
      }
    }

    return true;
  }

  /**
   *
   * @param obj
   * @param prefix
   * @param lowerFirstChar 将结果的第一个字符编程小写
   */
  static substringFieldKeyByPrefix(
    obj: any,
    prefix: string,
    lowerFirstChar: boolean = true,
    deleteOldKey: boolean = true,
  ) {
    const keys = Object.keys(obj);
    for (const key of keys) {
      if (key.startsWith(prefix)) {
        const value = obj[key];
        let newKey = key.substring(prefix.length);
        if (lowerFirstChar) {
          newKey = newKey.substring(0, 1).toLowerCase() + newKey.substring(1);
        }
        obj[newKey] = value;
        if (deleteOldKey) {
          delete obj[key];
        }
      }
    }
  }

  static addFieldKeyPrefix(obj: any, prefix: string) {
    const keys = Object.keys(obj);
    for (const key of keys) {
      obj[prefix + key] = obj[key];
    }
  }

  static removeFieldValueByFieldKeyPrefix(obj: any, prefix: string) {
    const keys = Object.keys(obj);
    for (const key of keys) {
      if (key.startsWith(prefix)) {
        delete obj[key];
      }
    }
  }

  /**
   * 获取对象的值， 支持字符串或数组
   * 如  var obj = {user:{age:17}}, getValueByDataIndex(obj, ['user','age'])
   * @param obj
   * @param dataIndex
   */
  static getValueByDataIndex(obj: any, dataIndex: string | string[]) {
    if (typeof dataIndex === 'string') {
      let strDataIndex = <string>dataIndex;
      let value = obj[strDataIndex];
      if (value != null) {
        return value;
      }

      if (strDataIndex.indexOf('.') >= 0) {
        dataIndex = strDataIndex.split('.');
      }
    }

    let value = obj;
    for (let key of dataIndex) {
      value = value[key];
      if (value == null) {
        break;
      }
    }
    return value;
  }

  /**
   *  将嵌套对象转为简单对象，字段名使用 点号分割
   *
   *  如 obj = {user:{age:18}}
   *  转换为 obj = {"user.age": 18}
   *
   */
  static flatBean(obj: any) {
    const keys = Object.keys(obj);
    for (let key of keys) {
      // TODO
    }
  }

  static unFlatBean(obj: any) {
    if (obj == null) {
      return null;
    }
    const newObj: any = {};
    const keys = Object.keys(obj);
    for (let key of keys) {
      if (key.indexOf('.') == -1) {
        newObj[key] = obj[key];
      }

      let subKeys = key.split('.'); // user.age
      let value = obj[key];

      let subObj = newObj;
      for (let i = 0; i < subKeys.length - 1; i++) {
        let subKey = subKeys[i]; // ['user','age']

        if (Object.keys(subObj).indexOf(subKey) == -1) {
          subObj[subKey] = {};
        }

        subObj = subObj[subKey];
      }
      subObj[subKeys[subKeys.length - 1]] = value;
    }

    console.log('新对象', obj, newObj);

    return newObj;
  }

  /**
   * 通过索引素组找到对象的值
   * @param obj 例如 {user:{name:"zhangsan"}}
   * @param dataIndex 例如 ["user", "name"]
   */
  static findValueByDataIndex(obj: object, dataIndex: string[]): any {
    if (obj == null) {
      return;
    }
    let v: any = obj;
    for (let i = 0; i < dataIndex.length; i++) {
      const key: string = dataIndex[i];
      v = v[key];
      if (v === null) {
        return;
      }
    }
    return v;
  }
}
