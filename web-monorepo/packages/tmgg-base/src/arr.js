export function contains(arr, item) {
    return arr.indexOf(item) !== -1;
}

export function containsAny(arr, ...items) {
    for (let item of items) {
        if (arr.contains(item)) {
            return true
        }
    }
    return false;
}

export function add(arr, item) {
    arr.push(item)
}

export function addAt(arr, index, item) {
    arr.splice(index, 0, item);
}

/**
 * 将一个数组追加到尾
 * @param index
 * @param item
 */
export function addAll(arr, items) {
    for (let i = 0; i < items.length; i++) {
        arr.push(items[i])
    }
}

export function removeAt(arr, index) {
    arr.splice(index, 1)
}

export function remove(arr, item) {
    const index = arr.indexOf(item);
    if (index !== -1) {
        removeAt(arr, index)
    }
}

/**
 * 清空
 */
export function clear(arr) {
    arr.length = 0
}

/**
 * 截取数组
 * @param fromIndex  low endpoint (inclusive) of the subList toIndex
 * @param toIndex  high endpoint (exclusive) of the subList
 */
export function sub(arr, fromIndex, toIndex) {
    return arr.slice(fromIndex, toIndex);
}

export function swap(arr, item1, item2) {
    const index1 = arr.indexOf(item1);
    const index2 = arr.indexOf(item2);

    arr[index1] = item2;
    arr[index2] = item1;
}
export function insert(arr, index, item) {
    arr.splice(index, 0, item);
}
export function pushIfNotExist(arr, item) {
    const index = arr.indexOf(item);
    if (index == -1) {
        arr.push(item);
    }
}

export function pushAll(arr, newArr) {
    for (let i = 0; i < newArr.length; i++) {
        arr.push(newArr[i]);
    }
}

/**
 * 获取对象数组中某一属性值最大的对象
 * @param arr
 */
export function maxBy(arr, key){
    if (arr == null || arr.length === 0) {
        return undefined;
    }

    let maxElement;
    let maxValue = -Infinity;

    for (const element of arr) {
        const value = element[key];
        if (value > maxValue) {
            maxValue = value;
            maxElement = element;
        }
    }

    return maxElement;
}

/**
 * 数组去重
 * @param arr
 * @returns {any[]}
 */
export function unique(arr) {
    return [...new Set(arr)]
}