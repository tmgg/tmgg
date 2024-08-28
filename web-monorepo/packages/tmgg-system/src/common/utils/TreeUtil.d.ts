import { ArrayUtil } from './ArrayUtil';

export class TreeUtil {
  // 遍历每个节点的children，并执行fn函数
  static every(tree, fn);

  static findByKey(list, key);
  static findByKeyList(treeData, keyList);

  static getOnDropData(info, tree);

  static find(list, fn);

  static convertSimpleListToTree(list);

  static getSimpleList(treeNodeList);

  static getChild(treeNode, buffer);

  static getKeyList(tree, value);
}
