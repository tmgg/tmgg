/**
 * 支持 key|id, parentKey|pid 两种模式
 */
import { ArrayUtil } from './ArrayUtil';

export class TreeUtil {
  // 遍历每个节点的children，并执行fn函数
  static every(tree, fn) {
    for (let item of tree) {
      fn(item);
      if (item.children && item.children.length) {
        TreeUtil.every(item.children, fn);
      }
    }
  }

  static findByKey(list, key) {
    return this.find(list, (item) => item.key == key || item.id == key);
  }

  static findByKeyList(treeData, keyList) {
    const itemList = [];
    TreeUtil.every(treeData, (item) => {
      const key = item.key || item.id;

      if (ArrayUtil.contains(keyList, key)) {
        itemList.push(item);
      }
    });

    return itemList;
  }

  static find(list, fn) {
    if (list == null || list.length == 0) {
      return null;
    }

    for (let i = 0; i < list.length; i++) {
      let item = list[i];
      if (fn(item)) {
        return item;
      }

      let target = this.find(item.children, fn);
      if (target != null) {
        return target;
      }
    }

    return null;
  }

  // id, pid
  static convertSimpleListToTree(list) {
    const map = {};

    const result = [];
    for (let item of list) {
      map[item.id] = item;
    }

    for (let item of list) {
      const p = map[item.pid];
      if (p) {
        if (p.children == null) {
          p.children = [];
        }
        p.children.push(item);
      }

      if (item.pid == null || p == null) {
        result.push(item);
      }
    }

    return result;
  }

  static getSimpleList(treeNodeList) {
    const buffer = [];

    if (treeNodeList != null) {
      treeNodeList.forEach((t) => {
        buffer.push(t);
        TreeUtil.getChild(t, buffer);
      });
    }
    return buffer;
  }

  static getChild(treeNode, buffer) {
    if (treeNode.children != null && treeNode.children.length > 0) {
      treeNode.children.forEach((c) => {
        buffer.push(c);
        TreeUtil.getChild(c, buffer);
      });
    }
    return buffer;
  }

  static getKeyList(tree, value) {
    const list = TreeUtil.getSimpleList(tree);

    const map = {};
    list.forEach((t) => {
      map[t.key || t.id] = t;
    });

    const t = map[value];
    if (t == null) {
      return [];
    }

    const keys = [t.key || t.id];
    let parent = map[t.parentKey || t.pid];
    while (parent != null) {
      keys.push(parent.key || t.id);
      parent = map[parent.parentKey || t.pid];
    }

    return keys.reverse();
  }

  static getOnDropData(info, tree) {
    const dragKey = info.dragNode.key;
    const dropKey = info.node.key;
    const { dropPosition, dropToGap } = info;

    const dropNode = TreeUtil.findByKey(tree, dropKey);
    const data = {
      id: dragKey,
    };

    if (dropToGap) {
      data.pid = dropNode.parentKey;
      data.seq = dropPosition;
    } else {
      // 缝隙，第一个子节点
      data.pid = dropKey;
      data.seq = 0;
    }

    return data;
  }
}
