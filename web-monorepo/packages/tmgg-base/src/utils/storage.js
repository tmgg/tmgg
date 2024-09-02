import {StrUtil} from "./str";


const STORAGE_KEY = "__storage"

export function getAll() {
    let hexString = localStorage.getItem(STORAGE_KEY) || "";
    if (!hexString) {
        return {}
    }
    return JSON.parse(StrUtil.decrypt(hexString))
}


export function keys() {
    return getAll().keys();
}

export const StorageUtil = {
 get(key, defaultValue = null) {
        let v = getAll()[key];
        if(v == null){
            v = defaultValue;
        }
        return v
    },

    set(key, value) {
        let data = getAll();
        data[key] = value
        const dataStr = JSON.stringify(data)
        localStorage.setItem(STORAGE_KEY, StrUtil.encrypt(dataStr))
    }

}



