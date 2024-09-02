// 数据结构 - tree

// 为方便起见，默认 3个字段 id， pid， children

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
