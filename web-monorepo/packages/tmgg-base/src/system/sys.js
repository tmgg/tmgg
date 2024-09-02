import {storage} from "../storage";


const SITE_INFO_KEY = "siteInfo"

export function setSiteInfo(data){
    return storage.set(SITE_INFO_KEY,data)
}
export  function getSiteInfo(){
    return storage.get(SITE_INFO_KEY)
}




const LOGIN_INFO_KEY = "loginInfo"
export function setLoginInfo(data){
    return storage.set(LOGIN_INFO_KEY,data)
}

export function getLoginInfo(){
    return storage.get(LOGIN_INFO_KEY)
}



const DICT_INFO_KEY = "dictInfo"
export function setDictInfo(data){
    return storage.set(DICT_INFO_KEY,data)
}

export function getDictInfo(){
    return storage.get(DICT_INFO_KEY)
}
