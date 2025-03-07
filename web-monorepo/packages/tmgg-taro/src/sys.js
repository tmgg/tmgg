//  经常修改的参数放前面
import Taro from "@tarojs/taro";



const isWeb = Taro.getEnv() === "WEB"
console.log('是否网页', isWeb)

let isProd = process.env.NODE_ENV === 'production';
console.log('开发环境？', process.env.NODE_ENV)


const globalData = {}

export function getServerUrl() {
  return process.env.TARO_APP_SERVER_URL ;
}



let appId = null
export function getAppId(){
  if(isWeb){
    return 'web'
  }

  if(appId){
    return appId
  }
   appId =  Taro.getAccountInfoSync().miniProgram.appId;
  return appId;
}
