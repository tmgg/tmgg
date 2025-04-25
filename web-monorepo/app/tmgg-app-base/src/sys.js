//  经常修改的参数放前面
import Taro from "@tarojs/taro";

const ENV_PROD = process.env.NODE_ENV === 'production';
const ENV_SERVER_URL = process.env.TARO_APP_SERVER_URL;


class SysUtilClass {
    _appId = null

    _token = null


    isProd() {
        return ENV_PROD
    }

    isWeb() {
        return Taro.getEnv() === "WEB"
    }

    getServerUrl() {
        return ENV_SERVER_URL;
    }


    getAppId() {
        if (this._appId == null) {
            if (this.isWeb()) {
                return null
            }
            this._appId = Taro.getAccountInfoSync().miniProgram.appId;
        }
        return this._appId
    }

    setToken(token, expireTime) {
        this._token = token;
        Taro.setStorageSync('token', token)
        Taro.setStorageSync('tokenExpireTime', expireTime)
    }

    getToken() {
        if (this._token == null) {
            this._token = Taro.getStorageSync('token');
        }
        return this._token;
    }

    /**
     * 判断token是否失效
     */
    isTokenValid() {
        let expireTime = Taro.getStorageSync('tokenExpireTime');
        if (expireTime) {
            expireTime = Number(expireTime)
            const d = new Date();
            const now = d.getTime();
            if(now < expireTime) {
                return true;
            }
        }

        return false
    }


}

export const SysUtil = new SysUtilClass();
