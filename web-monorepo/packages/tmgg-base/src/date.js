import {str} from "./str.js";


export function year(date) {
    return date.getFullYear();
}

/**
 *  获取月份， 自动补0
 * @param date
 * @returns {*}
 */
export function month(date) {
    const n = date.getMonth() + 1; // （注意月份从0开始，所以要加1）
    return str.pad(n, 2)
}

/**
 * 获取日期，
 * @param date
 */
export function date(date) {
    return str.pad(date.getDate(), 2)
}

/**
 * 小时， 24进制
 * @param date
 * @returns {string}
 */
export function hour(date) {
    return str.pad(date.getHours(), 2);
}

export function minute(date) {
    return str.pad(date.getMinutes(), 2);
}

export function second(date) {
    return str.pad(date.getSeconds(), 2);
}

export function formatDate(d) {
    return year(d) + '-' + month(d) + "-" + date(d)
}

export function formatTime(d) {
    return hour(d) + ':' + minute(d) + ":" + second(d)
}

export function formatDateTime(d) {
    return formatDate(d) + " " + formatTime(d)
}

/**
 *
 * @param d
 * @returns {string} 2020年1月30日
 */
export function formatDateCn(d) {
    return year(d) + '年' + (d.getMonth() + 1) + '月' + d.getDate() + '日'
}

/***
 当前时间, 如 2022-01-23 11:59:59
 */
export function now() {
    return formatDateTime(new Date());
}

/**
 * 当前日期 ，如 2022-01-23
 *
 */
export function today() {
    return formatDate(new Date());
}

export function thisYear() {
    return year(new Date())
}

export function thisMonth() {
    return month(new Date())
}


/**
 * 显示友好时间， 如 2小时前， 1周前
 * @param pastDate 日期, 支持Date， String， Number
 */
export function friendlyTime(pastDate) {
    if (pastDate == null) {
        return
    }
    if (!(pastDate instanceof Date)) {
        pastDate = new Date(pastDate)
    }

    const currentDate = new Date();
    let elapsedMilliseconds = currentDate - pastDate;
    const suffix = elapsedMilliseconds > 0 ? "前" : "后";
    elapsedMilliseconds = Math.abs(elapsedMilliseconds)

    // 计算年、月、日、小时、分钟和秒的差值
    const elapsedYears = Math.floor(elapsedMilliseconds / (1000 * 60 * 60 * 24 * 365));
    const elapsedMonths = Math.floor(elapsedMilliseconds / (1000 * 60 * 60 * 24 * 30));
    const elapsedDays = Math.floor(elapsedMilliseconds / (1000 * 60 * 60 * 24));
    const elapsedHours = Math.floor(elapsedMilliseconds / (1000 * 60 * 60));
    const elapsedMinutes = Math.floor(elapsedMilliseconds / (1000 * 60));
    const elapsedSeconds = Math.floor(elapsedMilliseconds / 1000);

    // 根据差值选择友好的格式
    if (elapsedYears >= 1) {
        return `${elapsedYears} 年${suffix}`;
    }
    if (elapsedMonths >= 1) {
        return `${elapsedMonths} 个月${suffix}`;
    }
    if (elapsedDays >= 7) {
        const weeks = Math.floor(elapsedDays / 7);
        return `${weeks} 周${suffix}`;
    }
    if (elapsedDays >= 1) {
        const days = elapsedDays;
        return `${days} 天${suffix}`;
    }
    if (elapsedHours >= 1) {
        return `${elapsedHours} 小时${suffix}`;
    }
    if (elapsedMinutes >= 1) {
        return `${elapsedMinutes} 分钟${suffix}`;
    }
    return `${elapsedSeconds} 秒${suffix}`;
}

/**
 * 总共耗时, 如 3分5秒
 * @param time 数字 （Date.getTime）
 * @returns {string|null}
 */
export function friendlyTotalTime(time) {
    if (time == null || time === '-') {
        return null
    }
    let seconds = time / 1000;

    seconds = Math.floor(seconds)

    if (seconds < 60) {
        return seconds + '秒';
    }

    let min = seconds / 60;
    seconds = seconds % 60;

    min = Math.floor(min);
    seconds = Math.floor(seconds)

    return min + '分' + seconds + '秒'
}

