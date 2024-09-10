import {StorageUtil} from "../utils";

const SITE_INFO_KEY = "siteInfo"
const LOGIN_INFO_KEY = "loginInfo"
const DICT_INFO_KEY = "dictInfo"
const TOKEN_KEY = "X-Auth-Token"; // 认证token，存储、请求头、响应头保持一致

let  SERVER_URL = "/"

//  process?.env?.API_BASE_URL
export const SysUtil = {

    /**
     * 服务器端的地址， 以 /结尾
     * @returns {string}
     */
    getServerUrl() {
        return SERVER_URL
    },

    setToken(data) {
        return StorageUtil.set(TOKEN_KEY, data)
    },

    getToken() {
        return StorageUtil.get(TOKEN_KEY)
    },

    getTokenKey(){
      return TOKEN_KEY
    },

    getHeaders() {
        const token =this.getToken();
        const headers = {}
        headers[TOKEN_KEY] = token;
        return headers;
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
        return StorageUtil.get(LOGIN_INFO_KEY) || {}
    },


    setDictInfo(data) {
        return StorageUtil.set(DICT_INFO_KEY, data)
    },

    getDictInfo() {
        return StorageUtil.get(DICT_INFO_KEY)
    }
}


