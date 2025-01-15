import {showToast, request,showModal} from "@tarojs/taro";
import {SysUtil} from "@tmgg/tmgg-commons-lang";



const defaultRequestConfig = {

    autoHandleErrors: true,

    /**
     * 是否自动显示操作成功时的消息(message字段）
     */
    autoShowSuccessMessage: true,

    /**
     * 转换数据， 直接返回结果数据的data字段
     */
    transformData: true,
}
function showErrorMessage(title, msg) {
    showModal({
        title,
        content: msg,
        showCancel:false
    })
}
/**
 *
 * @param url {string}
 * @param params
 * @param config
 * @returns {Promise<unknown>}
 */
function send(url, params, config) {
    return new Promise((resolve, reject) => {
        if(url.startsWith('/')){
            url  = url.substring(1)
        }
        url = SysUtil.getServerUrl() + url;
        console.log('请求链接', url)


        request({
            url: url,
            method: config.method,
            data: params,
            header: {
                'X-Client-Type':'app',
                'X-Auth-Token': SysUtil.getToken(),
              //  'Authorization': getToken(),
              //  'appId': getAppId()
            },
            success: res => {
                console.log('响应结果', res)
                let ajaxResult = res.data.data;
                resolve(ajaxResult)
            },
            fail: fail => {
                console.log('HttpClient fail ' + fail)
                showErrorMessage()
                reject(fail)
            }
        })
    });
}


/**
 *  防止双斜杠出现
 */
function _replaceUrl(url) {
    if (url.startsWith("//")) {
        return url.substring(1)
    }
    return url;
}




export const HttpUtil = {

    get(url, params = null, config = defaultRequestConfig) {
        config = Object.assign({},defaultRequestConfig, config)
        config.method = 'GET'
        url = _replaceUrl(url)
        return send(url, params, config)
    },

    post(url, data, params = null, config = defaultRequestConfig) {
        config = Object.assign(config, defaultRequestConfig)
        url = _replaceUrl(url)
        config.method = 'POST';
        return send(url, data, config)
    },

    postForm(url, data, config = defaultRequestConfig) {
        config = Object.assign(config, defaultRequestConfig)
        url = _replaceUrl(url)
        return send(url, data, config)
    },

    /**
     * 分页请求, 为antd的ProTable
     * @param url
     * @param params
     * @param sort
     * @returns {Promise<unknown>}
     */
    pageData(url, params, sort) {
        const {current, pageSize, keyword, ...data} = params;

        const pageParams = {
            page: current,
            size: pageSize,
            keyword
        }

        if (sort) {
            let keys = Object.keys(sort);
            if (keys.length > 0) {
                let key = keys[0];
                let dir = sort[key] === 'ascend' ? 'asc' : 'desc';
                pageParams.sort = key + "," + dir
            }
        }


        return this.post(url, data, pageParams)
    },

}
