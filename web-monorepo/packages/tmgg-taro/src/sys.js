//  经常修改的参数放前面
import Taro from "@tarojs/taro";



//const isWeb = Taro.getEnv() === "WEB"
let isProd = process.env.NODE_ENV === 'production';
console.log('开发环境？', process.env.NODE_ENV)


const globalData = {}

export function getServerUrl() {
  return process.env.SERVER_URL ;
}

export function getToken() {
  return globalData['token'] || '';
}

export function setToken(v) {
  return globalData['token'] = v;
}

export function getAppId(){
  let appId =  Taro.getAccountInfoSync().miniProgram?.appId;
  return appId;
}
