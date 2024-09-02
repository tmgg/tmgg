import {StorageUtil} from "../utils";

const SITE_INFO_KEY = "siteInfo"
const LOGIN_INFO_KEY = "loginInfo"
const DICT_INFO_KEY = "dictInfo"
export const SysUtil = {

    getServerUrl() {
        const serverUrl = process.env.API_BASE_URL
        return serverUrl
    },

    setSiteInfo(data) {
        return StorageUtil.set(SITE_INFO_KEY, data)
    },

    getSiteInfo() {
        return StorageUtil.get(SITE_INFO_KEY) || {}
    },

    setLoginInfo(data) {
        return StorageUtil.set(LOGIN_INFO_KEY, data)
    },

    getLoginInfo() {
        return StorageUtil.get(LOGIN_INFO_KEY)
    },


    setDictInfo(data) {
        return StorageUtil.set(DICT_INFO_KEY, data)
    },

    getDictInfo() {
        return StorageUtil.get(DICT_INFO_KEY)
    }
}


