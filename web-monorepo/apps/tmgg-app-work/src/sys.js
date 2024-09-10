//  经常修改的参数放前面
import Taro from "@tarojs/taro";

const DEV_URL = "http://127.0.0.1:82";

const ALWAYS_PROD = false;

//const isWeb = Taro.getEnv() === "WEB"
let isProd = process.env.NODE_ENV === 'production';


const PROD_URL = "https://ky-health-service.ztstc.cn"
console.log('开发环境？', process.env.NODE_ENV)


const globalData = {}


export function getServerUrl() {
  if (ALWAYS_PROD) {
    return PROD_URL;
  }

  return isProd ? PROD_URL : DEV_URL;
}

export function getToken() {
  return globalData['token'] || '';
}

export function setToken(v) {
  return globalData['token'] = v;
}

export function getAppId(){
  let appId =  Taro.getAccountInfoSync().miniProgram.appId;
  return appId;
}
