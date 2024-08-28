import axios from "axios";
import {storage} from "./storage.js";
import {Modal} from "antd";


const axiosInstance = axios.create({
    withCredentials: true, // 带cookie
    baseURL: '/',
})

const AUTH_STORE_KEYS = ['jwt', 'appToken', 'token', 'Authorization'];


function getToken() {
    for (let key of AUTH_STORE_KEYS) {
        let v = localStorage.getItem(key);
        if (v) {
            return v;
        }
    }

    return storage.get("HD:Authorization")
}

axiosInstance.interceptors.request.use(
    config => {
        // 增加header

        let token = getToken();
        if (token) {
            config.headers['Authorization'] = token;
        }

        let globalHeaders = getGlobalHeaders();
        for (let key in globalHeaders) {
            config.headers[key] = globalHeaders[key];
        }

        return config;
    }
);

axiosInstance.interceptors.response.use(res => {
    const {data, headers} = res;

    if (data && (typeof data) === 'object') {
        data._headers = headers
    }

    return data;
})

/**
 *
 * @param msg
 * @param error 原始错误信息
 */

let globalErrorMessageHandler = (msg, error) => {
    console.log('请求异常', msg, error)
    Modal.error({
        title:'网络请求异常',
        content: error.status + ":" + msg
    })
}


addErrorInterceptor()




/**
 *
 * @param errorMessageHandler
 * @param autoReject 如何响应里面的success 字段为false，则reject
 */
function init({errorMessageHandler = null, autoReject = true}) {
    if (errorMessageHandler) {
        globalErrorMessageHandler = errorMessageHandler
    }

    if (autoReject) {
        axiosInstance.interceptors.response.use(response => {
            // 注意，response是具体数据，不带header等
            if (response.success === false) {
                globalErrorMessageHandler(response.message, response)
                return Promise.reject(response)
            }

            return response;
        })
    }
}


function addErrorInterceptor() {
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


    /**
     * axios 的错误代码
     * 来源 AxiosError.ERR_NETWORK
     * 直接使用的chatgpt 转换为js对象并翻译
     */
    const AXIOS_CODE_MESSAGE = {
        ERR_FR_TOO_MANY_REDIRECTS: "错误：重定向过多",
        ERR_BAD_OPTION_VALUE: "错误：选项值无效",
        ERR_BAD_OPTION: "错误：无效选项",
        ERR_NETWORK: "错误：网络错误",
        ERR_DEPRECATED: "错误：已弃用",
        ERR_BAD_RESPONSE: "错误：响应错误",
        ERR_BAD_REQUEST: "错误：无效请求",
        ERR_NOT_SUPPORT: "错误：不支持",
        ERR_INVALID_URL: "错误：无效的URL",
        ERR_CANCELED: "错误：已取消",
        ECONNABORTED: "连接中止",
        ETIMEDOUT: "连接超时"
    }

    axiosInstance.interceptors.response.use(
        null,
        error => {
            // 对响应错误做点什么
            let {message, code, response} = error;
            let msg = response ? STATUS_MESSAGE[response.status] : AXIOS_CODE_MESSAGE[code];

            globalErrorMessageHandler(msg || message, error)

            return Promise.reject(error)
        })
}

 function setGlobalHeader(key, value) {
    storage.set("HD:" + key, value)
}

 function getGlobalHeaders() {
    const result = {}
    let all = storage.getAll();
    for (let key in all) {
        const value = all[key];
        if (key.startsWith("HD:")) {
            key = key.substring("HD:".length)
            result[key] = value
        }
    }
    return result;
}

/**
 *  防止双斜杠出现
 */
function makeUrl(url) {
    if (url.startsWith("//")) {
        return url.substring(1)
    }
    return url;
}

 function get(url, params = null) {
    url = makeUrl(url)
    return axiosInstance.get(url, {params})
}

 function post(url, data, params = null) {
    url = makeUrl(url)
    return axiosInstance.post(url, data, {params})
}

function postForm(url, data) {
    url = makeUrl(url)
    return axiosInstance.postForm(url, data)
}


/**
 * 分页请求, 正对前端为antd的ProTable
 * @param url
 * @param params
 * @param sort
 * @returns {Promise<unknown>}
 */
function requestPageData(url, params, sort, method = 'GET') {
    params.pageNumber = params.current;
    delete params.current
    if (sort) {
        let keys = Object.keys(sort);
        if (keys.length > 0) {
            let key = keys[0];
            let dir = sort[key] === 'ascend' ? 'asc' : 'desc';
            params.orderBy = key + "," + dir
        }
    }


    function convert(response) {
        // 判断是否被包装
        if (response.data != null && response.data.totalElements != undefined) {
            response = response.data
        }
        // 按pro table 的格式修改数据结构
        let pageable = {};
        pageable.data = response.content;
        pageable.success = true;
        pageable.total = parseInt(response.totalElements);
        return pageable;
    }

    if (method === 'GET') {
        return get(url, params).then(convert)
    } else if (method === "POST") {
        return post(url, params).then(convert)
    } else {
        throw new Error('不支持 Method：' + method)
    }


}

function downloadFile(url, params) {
    console.log('下载中...')

    let config = {
        url,
        params,
        responseType: 'blob',
    };
    return new Promise((resolve, reject) => {


        axiosInstance(config).then(data => {
            console.log('下载数据结束', data);

            const headers = data._headers

            // 获取文件名称
            var contentDisposition = headers.get('content-disposition');
            if (headers == null || contentDisposition == null) {
                showError('获取文件信息失败');
                reject(null)
                return
            }


            let regExp = new RegExp('filename=(.*)');
            // @ts-ignore
            const result = regExp.exec(contentDisposition);

            // @ts-ignore
            let filename = result[1];

            filename = decodeURIComponent(filename);
            filename = filename.replaceAll('"', '');
            filename = filename.replace(/^["](.*)["]$/g, '$1')


            const url = window.URL.createObjectURL(new Blob([data]));
            const link = document.createElement('a');
            link.style.display = 'none';

            link.href = url;
            link.download = decodeURI(filename); // 下载后文件名

            document.body.appendChild(link);
            link.click();

            document.body.removeChild(link); // 下载完成移除元素
            window.URL.revokeObjectURL(url);
        })
    })

}

export const http = {
    axiosInstance,
    getToken,
    init,
    setGlobalHeader,
    addErrorInterceptor,
    downloadFile,
    requestPageData,
    postForm,
    post,
    get,
    getGlobalHeaders
}
