import Taro from "@tarojs/taro";
import {SysUtil} from "./sys";



function getHeader(){
    const header = {}
    let token = SysUtil.getToken();
    if(token){
        header['Authorization'] = "Bearer " + token
    }
    return header
}

function request(url,params,option){
    return new Promise((resolve, reject) => {
        Taro.request({
            url:SysUtil.getServerUrl() + url,
            data:params,
            header: {
                ... option.header,
                ...getHeader()
            },
            method: option.method,
            success: res => {
                if(res.statusCode !== 200){
                    reject({code: res.statusCode, success:false})
                    return
                }
                const r = res.data
                if(r.success === false){
                    reject(r)
                    return;
                }

                resolve(r)
            },
            fail: fail => {
                console.log('请求失败：' + fail)
                reject({success:false,message: fail.errMsg})
            }
        })
    })

}


export class HttpUtil {


    static get(url, params = {}) {
        return  request(url, params, {
            method:'GET'
        })
    }


    static post(url, params = {}) {
        return request(url,params,{
            method: 'POST',
            header: {
                'content-type': 'application/json', // 默认值
            },
        })
    }

    static postForm(url, data) {
        return request(url,data,{
            method: 'POST',
            header: {
                'content-type': 'application/x-www-form-urlencoded',
            },
        })

    }

    static uploadFile(url, filePath) {
        return new Promise((resolve, reject) => {
            Taro.uploadFile({
                url: SysUtil.getServerUrl() + url,
                filePath: filePath,
                name: "file",
                formData: {},
                header:getHeader(),
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
