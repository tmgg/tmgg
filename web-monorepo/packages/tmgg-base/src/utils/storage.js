import {StrUtil} from "./str";


const STORAGE_KEY = "__storage"




export const StorageUtil = {
getAll() {
        let hexString = localStorage.getItem(STORAGE_KEY) || "";
        if (!hexString) {
            return {}
        }
        return JSON.parse(StrUtil.decrypt(hexString))
    },
  keys() {
        return this.getAll().keys();
    },

 get(key, defaultValue = null) {
        let v = this.getAll()[key];
        if(v == null){
            v = defaultValue;
        }
        return v
    },

    set(key, value) {
        let data = this.getAll();
        data[key] = value
        const dataStr = JSON.stringify(data)
        localStorage.setItem(STORAGE_KEY, StrUtil.encrypt(dataStr))
    }

}



