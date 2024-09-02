import {storage} from "../storage";


const SITE_INFO_KEY = "siteInfo"
export  function getSiteInfo(){
    return storage.get(SITE_INFO_KEY)
}

export function setSiteInfo(siteInfo){
    return storage.set(SITE_INFO_KEY,siteInfo)
}
