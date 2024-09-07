import Taro from "@tarojs/taro";
import {getAppId, getServerUrl, getToken} from "../sys";
export default class HttpClient {



  static get(url, params = {}) {
    return new Promise((resolve, reject) => {
      Taro.request({
        url: getServerUrl() + url,
        data: params,
        header: {
          'Authorization': getToken(),
          'appId': getAppId()
        },
        success: res => {
          resolve(res.data)
        },
        fail: fail => {
          console.log('HttpClient fail ' + fail)
          Taro.showModal({title: '请求异常', content: fail.errMsg, showCancel: false})
          reject(fail)
        }
      })
    });
  }


  static post(url, params = {}) {
    return new Promise((resolve, reject) => {
      Taro.request({
        method: 'POST',
        url: getServerUrl() + url,
        data: params,
        header: {
          'content-type': 'application/json', // 默认值
          'Authorization': getToken(),
          'appId': getAppId()
        },
        success: res => {
          resolve(res.data)
        },
        fail: res => {
          Taro.showModal({title: '请求异常', content: res.errMsg, showCancel: false})
          reject(res)
        }
      })
    });
  }

  static postForm(url, data) {
    return new Promise((resolve, reject) => {
      Taro.request({
        method: 'POST',
        url: getServerUrl() + url,
        data: data,
        header: {
          'content-type': 'application/x-www-form-urlencoded',
          'Authorization': getToken(),
          'appId': getAppId()
        },
        success: res => {
          resolve(res.data)
        },
        fail: res => {
          Taro.showModal({title: '请求异常', content: res.errMsg, showCancel: false})
          reject(res)
        }
      })
    });
  }

  static uploadFile(url, filePath){
    return new Promise((resolve, reject) => {
      Taro.uploadFile({
        url: getServerUrl() + url,
        filePath: filePath,
        name: "file",
        formData: {},
        header: {
          'Authorization': getToken(),
          'appId': getAppId()
        },
        success(res) {
         resolve(JSON.parse(res.data))
        },
        fail:res=>{
          Taro.showModal({title: '上传异常', content: res.errMsg, showCancel: false})

          reject(res)
        }
      })
    })

  }
}
