import {SysUtil} from "@tmgg/tmgg-common";
import {showToast, request,showModal} from "@tarojs/taro";



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

function showSuccessMessage(msg) {
    showToast({
        title: msg
    })
}

function showErrorMessage(title, msg) {
    showModal({
        title,
        content: msg,
        showCancel:false
    })
}


const STATUS_MESSAGE = {
    200: '服务器成功返回请求的数据',
    201: '新增或修改数据成功',
    202: '一个请求已经进入后台排队（异步任务）',
    204: '删除数据成功',
    400: '发出的请求有错误，服务器没有进行新增或修改数据的操作',
    401: '请求需要登录',
    403: '权限不足',
    404: '接口未定义',
    406: '请求的格式不可得',
    410: '请求的资源被永久删除，且不会再得到的',
    422: '当创建一个对象时，发生一个验证错误',
    500: '服务器发生错误，请检查服务器',
    502: '网关错误',
    503: '服务不可用，服务器暂时过载或维护',
    504: '网关超时',
};


function send(url, params, config) {
    return new Promise((resolve, reject) => {
        const serverUrl = SysUtil.getServerUrl();
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
        config = Object.assign(config, defaultRequestConfig)
        url = _replaceUrl(url)
        return send(url, params, config)
    },

    post(url, data, params = null, config = defaultRequestConfig) {
        config = Object.assign(config, defaultRequestConfig)
        url = _replaceUrl(url)
        config.method = 'POST';
        return send(url, params, config)
    },

    postForm(url, data, config = defaultRequestConfig) {
        config = Object.assign(config, defaultRequestConfig)
        url = _replaceUrl(url)
        return send(url, params, config)
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


        function convertToProTableData(data) {
            return {
                data: data.content,
                success: true,
                total: parseInt(data.totalElements)
            }
        }

        return this.post(url, data, pageParams).then(convertToProTableData)
    },

}
