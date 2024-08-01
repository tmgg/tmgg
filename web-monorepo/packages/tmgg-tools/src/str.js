/**
 * 包含
 * @param subStr
 * @returns {boolean}
 */
export function contains(str, subStr) {
    if (!str) {
        return false
    }
    return str.includes(subStr);
}


/**
 * 判断字符串出现的个数
 * @param subStr
 */
export function count(str, subStr) {
    if (str == null || str.length == 0) {
        return 0;
    }
    let count = 0;
    let index = 0;

    while (true) {
        index = str.indexOf(subStr, index);
        if (index === -1) {
            break;
        }
        count++;
        index += subStr.length;
    }

    return count;
}


/**
 *  将字符串的首字母转换为大写
 */
export function capitalize(str) {
    if (str == null) {
        return str
    }
    return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * 颠倒字符串的顺序
 * @param str
 * @returns {*}
 */
export function reverse(str) {
    if (str == null) {
        return str;
    }
    return str.split('').reverse().join('');
}

/**
 * 截取字符串, 如果不包含，则原样返回
 * @param s
 * @param sub
 * @returns {string|null}
 */
export function subAfter(s, sub) {
    if (s == null) {
        return s;
    }
    const index = s.indexOf(sub)
    return index === -1 ? s : s.substring(index + 1);
}

export function subBefore(s, sub) {
    if (s == null) {
        return s;
    }
    const index = s.indexOf(sub)
    return index === -1 ? s : s.substring(0, index);
}

/**
 * 混淆
 * @param str
 * @returns {*}
 */
export function obfuscateString(str) {
    if (str == null) {
        return str
    }
    return str.split('').map(char => char + 1).join('');
}


/**
 * 补零
 * @param input 输入字符串说数字等
 * @param totalLen 总长度，不狗
 * @returns {string}
 */
export function pad(input, totalLen, padChar = '0') {
    if (input == null) {
        return padChar.repeat(totalLen)
    }
    var str = String(input);
    var zerosNeeded = totalLen - str.length;
    if (zerosNeeded > 0) {
        str = padChar.repeat(zerosNeeded) + str;
    }
    return str;
}


/**
 * 加密
 * @param str
 * @returns {string}
 */
export function encrypt(str) {
    if (str == null) {
        return str
    }
    let encrypted = '';
    for (let i = 0; i < str.length; i++) {
        let charCode = str.charCodeAt(i);
        charCode += 1;
        encrypted += ('00' + charCode.toString(16)).slice(-4); // 转换为四位的十六进制表示
    }
    return encrypted;
}


/**
 * 加密
 * @param str
 * @returns {string}
 * @deprecated  使用 encrypt
 */
export function encryptString(str) {
    return encrypt(str)
}


export function decrypt(hexString) {
    if (hexString == null) {
        return hexString;
    }
    let decrypted = '';
    for (let i = 0; i < hexString.length; i += 4) {
        let hexCharCode = hexString.substr(i, 4);
        let charCode = parseInt(hexCharCode, 16); // 将十六进制转换为十进制
        charCode -= 1
        decrypted += String.fromCharCode(charCode);
    }
    return decrypted;
}

export function decryptString(hexString) {
    return decrypt(hexString)
}


/**
 * 类似String.length, 区别在与中文字符占2位宽
 * 获取字符串长度，英文字符 长度1，中文字符长度2
 * @param {*} str
 */
export function getWidth(str) {
    if (str == null || str.length === 0) {
        return 0
    }
    return str.split('').reduce((pre, cur) => {
        const charCode = cur.charCodeAt(0);
        if (charCode >= 0 && charCode <= 128) {
            return pre + 1;
        }
        return pre + 2;
    }, 0);
}

export function cutByWidth(str, maxWidth) {
    let showLength = 0;
    return str.split('').reduce((pre, cur) => {
        const charCode = cur.charCodeAt(0);
        if (charCode >= 0 && charCode <= 128) {
            showLength += 1;
        } else {
            showLength += 2;
        }
        if (showLength <= maxWidth) {
            return pre + cur;
        }
        return pre;
    }, '');
};


/**
 *
 * @param str
 * @param len 字符长度，注：中文字符算2
 * @constructor
 */
export function ellipsis(str, len, suffix = '...') {
    if (str == null) {
        return str;
    }
    if (!isStr(str)) {
        return str;
    }

    // 快速判断2倍
    if (str.length * 2 < len) {
        return str
    }

    const fullLength = getWidth(str);
    let isTooLong = fullLength > len;

    if (!isTooLong) {
        return str;
    }

    return cutByWidth(str, len) + suffix;
}

export function isStr(value) {
    return typeof value === "string"
}


// 转驼峰
export function toCamelCase(str, firstLower = true) {
    let result = str.replace(/\_(\w)/g, function (all, letter) {
        return letter.toUpperCase();
    });

    if (firstLower) {
        result = result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    return result;
}

export function toUnderlineCase(name) {
    if (name == null) {
        return null;
    }
    let result = name.replace(/([A-Z])/g, '_$1').toLowerCase();
    if (result.startsWith('_')) {
        result = result.substring(1);
    }
    return result;
}

