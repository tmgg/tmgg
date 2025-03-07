import Taro from "@tarojs/taro";
import {getAppId, getServerUrl} from "./sys";

let cookie = null
const interceptor = function (chain) {
    const requestParams = chain.requestParams
    let {method, data, url, header} = requestParams
    if (header == null) {
        header = requestParams.header = {}
    }

    header['x-app-id'] = getAppId()
    if (cookie) {
        header['Cookie'] = cookie
    }
    console.log(`http ${method || 'GET'} --> ${url} data: `, data)

    return chain.proceed(requestParams)
        .then(res => {
            if (res.cookies.length > 0) {
                cookie = res.cookies[0]
            }
            console.log(`http <-- ${url} result:`, res)
            return res
        })
}
Taro.addInterceptor(interceptor)


export class HttpUtil {


    static get(url, params = {}) {
        return new Promise((resolve, reject) => {
            let fullUrl = getServerUrl() + url;
            console.log('请求路径', fullUrl)
            Taro.request({
                url: fullUrl,
                data: params,
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

    static uploadFile(url, filePath) {
        return new Promise((resolve, reject) => {
            Taro.uploadFile({
                url: getServerUrl() + url,
                filePath: filePath,
                name: "file",
                formData: {},


                success(res) {
                    resolve(JSON.parse(res.data))
                },
                fail: res => {
                    Taro.showModal({title: '上传异常', content: res.errMsg, showCancel: false})

                    reject(res)
                }
            })
        })

    }
}
