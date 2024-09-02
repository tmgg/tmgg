import {tools} from "../system";

const STORAGE_KEY = "__storage"

export function getAll() {
    let hexString = localStorage.getItem(STORAGE_KEY) || "";
    if (!hexString) {
        return {}
    }
    return JSON.parse(str.decrypt(hexString))
}

export function get(key, defaultValue = null) {
    let v = getAll()[key];
    if(v == null){
        v = defaultValue;
    }
    return v
}

export function set(key, value) {
    let data = getAll();
    data[key] = value
    const dataStr = JSON.stringify(data)
    localStorage.setItem(STORAGE_KEY, tools.str.encrypt(dataStr))
}

export function keys() {
    return getAll().keys();
}




