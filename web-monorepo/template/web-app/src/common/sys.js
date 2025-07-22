//  经常修改的参数放前面
import Taro from "@tarojs/taro";

const ENV_PROD = process.env.NODE_ENV === 'production';
const ENV_SERVER_URL = process.env.TARO_APP_SERVER_URL;


class SysUtilClass {

    isProd() {
        return ENV_PROD
    }

    isWeb() {
        return Taro.getEnv() === "WEB"
    }

    getServerUrl() {
        return ENV_SERVER_URL;
    }


}

export const SysUtil = new SysUtilClass();
