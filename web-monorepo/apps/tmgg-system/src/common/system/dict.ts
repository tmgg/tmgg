/***
 *
 * @param code
 */
import {StorageUtil} from '../utils';
import {Tag} from 'antd';
import React from 'react';
import hutool from "@moon-cn/hutool";

// 根据字典类型code返回字典数据列表， code 支持 驼峰或下划线（都转为下划线比较）
export function dictList(code: string) {
  const dictTypeTree = StorageUtil.get('DICT');
  if (dictTypeTree === undefined) {
    return [];
  }

  let filtered = dictTypeTree.filter((item: any) => {
      return item.code == code || hutool.str.toUnderlineCase(item.code) == hutool.str.toUnderlineCase(code);
    }
  );

  if (filtered.length == 0) {
    console.log('字典' + code + '不存在,请刷新或在后台添加字典');
    return [];
  }
  const tree = filtered[0].children;
  if (tree === undefined) {
    return [];
  }
  return tree;
}

export function dictMap(typeCode: string) {
  const list = dictList(typeCode);

  if (list == null) {
    console.log('未找到数据字典, code=' + typeCode);
    return {};
  }
  const map = {};
  list.forEach((i: any) => {
    const code = i.code;

    // @ts-ignore
    map[code] = i;
  });

  return map;
}

export function dictValue(typeCode: string, code: string) {
  const item = dictData(typeCode, code);
  if (item) {
    return item.name;
  }
}

export function dictValueTag(typeCode: string, dataCode: string) {
  if (dataCode == null) {
    return '';
  }
  const data = dictData(typeCode, dataCode);
  if (data != null) {
    const {name, color} = data;

    if (color == null) {
      return name;
    }

    // js 方式创建 ，等同于 <Tag color={color}>{name}</Tag>
    return React.createElement(Tag, {color: color}, name);
  }
  return '';
}

export function dictData(typeCode: string, code: string) {
  if (code == null) {
    return null;
  }
  const map = dictMap(typeCode);
  // @ts-ignore
  let item = map[code];
  if (item == null) {
    // @ts-ignore
    item = map[code + ''];
  }
  return item;
}
