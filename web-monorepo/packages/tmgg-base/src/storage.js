import {str} from "./str";

const STORAGE_KEY = "__storage"

 function getAll() {
    let hexString = localStorage.getItem(STORAGE_KEY) || "";
    if (!hexString) {
        return {}
    }
    return JSON.parse(str.decrypt(hexString))
}

 function get(key, defaultValue = null) {
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
    localStorage.setItem(STORAGE_KEY, str.encrypt(dataStr))
}

export function keys() {
    return getAll().keys();
}



export const storage = {
    getAll,get,set,keys
}
