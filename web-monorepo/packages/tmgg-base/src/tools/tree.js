// 数据结构 - tree

// 为方便起见，默认 3个字段 id， pid， children

import {arr} from "./arr";

/**
 * 将数组转换为树
 * @param data
 * @param idKey
 * @param pidKey
 * @returns {*[]}
 */
export function arrayToTree(data, idKey = 'id', pidKey = 'pid') {
    const map = {};
    for (let i = 0; i < data.length; i++) {
        const node = data[i];
        let id = node[idKey];
        map[id] = node;
    }

    const root = [];
    for (let i = 0; i < data.length; i++) {
        const node = data[i];
        let pid = node[pidKey];
        let parent = map[pid]
        if (parent) {
            if (parent.children == null) {
                parent.children = []
            }
            parent.children.push(node)
        } else {
            root.push(node)
        }
    }

    return root;
}

/**
 * 遍历
 * @param tree
 * @param callback
 */
export function traverseTree(tree, callback) {
    // 遍历当前层级的节点
    for (let i = 0; i < tree.length; i++) {
        const node = tree[i];
        callback(node); // 执行回调函数

        // 遍历子节点
        if (node.children && node.children.length > 0) {
            traverseTree(node.children, callback);
        }
    }
}

export function findByKey(key, list, keyName = "id") {
    for (let item of list) {
        if (item[keyName] === key) {
            return item;
        }
        if (item.children && item.children.length) {
            const rs = findByKey(key, item.children, keyName)
            if (rs) {
                return rs;
            }
        }
    }
}


export function every(tree, fn) {
    for (let item of tree) {
        fn(item);
        if (item.children && item.children.length) {
            TreeUtil.every(item.children, fn);
        }
    }
}

export function findByKey(list, key) {
    return this.find(list, (item) => item.key == key || item.id == key);
}

export function findByKeyList(treeData, keyList) {
    const itemList = [];
    every(treeData, (item) => {
        const key = item.key || item.id;

        if (arr.contains(keyList, key)) {
            itemList.push(item);
        }
    });

    return itemList;
}

export function find(list, fn) {
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
export function convertSimpleListToTree(list) {
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

export function getSimpleList(treeNodeList) {
    const buffer = [];

    if (treeNodeList != null) {
        treeNodeList.forEach((t) => {
            buffer.push(t);
            TreeUtil.getChild(t, buffer);
        });
    }
    return buffer;
}

export function getChild(treeNode, buffer) {
    if (treeNode.children != null && treeNode.children.length > 0) {
        treeNode.children.forEach((c) => {
            buffer.push(c);
            TreeUtil.getChild(c, buffer);
        });
    }
    return buffer;
}

export function getKeyList(tree, value) {
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

export function getOnDropData(info, tree) {
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
