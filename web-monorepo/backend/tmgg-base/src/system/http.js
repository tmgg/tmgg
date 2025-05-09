import axios from "axios";
import {message as Message, Modal} from "antd";
import {SysUtil} from "./sys";


function request(config) {

    const defaultConfig = {
        withCredentials: true, // 带cookie
        baseURL: SysUtil.getServerUrl(),

        // 附加的一些配置

        // 请求成功结果，返回data那一层（忽略 success,code ）
        transformData: true,

        // 自动显示成功消息（message)、失败消息(modal)
        showMessage: true,


        loadingCallback: null
    }


    let finalConfig = Object.assign({}, defaultConfig, config);

    const {transformData, showMessage, loadingCallback, ...axiosConfig} = finalConfig

    if (loadingCallback) {
        loadingCallback(true)
    }

    if (axiosConfig.url.startsWith("//")) {
        axiosConfig.url = axiosConfig.url.substring(1)
    }


    return new Promise((resolve, reject) => {
        axios(axiosConfig).then(response => {
            if (!transformData) {
                resolve(response);
                return;
            }

            const body = response.data;
            const {success, message, data} = body;

            if (success == null) { // 返回结果没有success标志，则原样
                resolve(body);
                return
            }


            if (showMessage) {
                if (success) {
                    if (message) {
                        Message.success(message)
                    }
                } else {
                    Modal.error({
                        title: '操作失败',
                        content: message
                    })
                }
            }

            if (success) {
                resolve(data)
            } else {
                reject(message)
            }


        }).catch(e => {
            if (showMessage) {
                let msg = e;
                if (e.response && e.response.data) {
                    const rs = e.response.data
                    msg = rs.message
                }
                if (msg == null && e.message) {
                    msg = e.message
                }

                Modal.error({
                    title: '操作失败',
                    content: msg
                })
            }

            reject(e)
        }).finally(() => {
            if (loadingCallback) {
                loadingCallback(false)
            }
        })
    })

}


export const HttpUtil = {
    request,

    get(url, params = null, showMessage, transformData) {
        let config = {
            url,
            method: 'GET',
            params,
        };
        if (showMessage != null) {
            config.showMessage = showMessage
        }
        if (transformData != null) {
            config.transformData = transformData
        }
        return request(config)
    },

    post(url, data, params = null, showMessage, transformData) {
        let config = {
            url,
            method: 'POST',
            params,
            data,
        };
        if (showMessage != null) {
            config.showMessage = showMessage
        }
        if (transformData != null) {
            config.transformData = transformData
        }
        return request(config)
    },

    postForm(url, data, showMessage, transformData) {
        console.warn('不推荐调用本方法,设计urlEncode等，对于特殊字符可能会有问题')
        let config = {
            url,
            method: 'POST',
            data,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
        };
        if (showMessage != null) {
            config.showMessage = showMessage
        }
        if (transformData != null) {
            config.transformData = transformData
        }
        return request(config)
    },

    /**
     * 分页请求, 为antd的ProTable
     * @param url
     * @param params
     * @param sort
     * @returns {Promise<unknown>}
     */
    pageData(url, params) {
        const {page, size, sort, ...data} = params;
        return this.post(url, data, {page, size, sort})
    },

    downloadFile(url, params, method = 'GET') {
        let config = {
            url,
            params,
            method,
            responseType: 'blob',
            transformData: false
        };
        if (method === 'GET') {
            config.params = params
        } else if (method === 'POST') {
            config.data = params;
        }

        request(config).then(res => {
            const {data: blob, headers} = res
            if (blob.type === 'application/json') {// 可能是错误了，否则不应该返回json
                const reader = new FileReader();
                reader.readAsText(blob, 'utf-8');
                reader.onload = function (e) {
                    let rs = JSON.parse(reader.result);
                    Modal.error({
                        title: '下载文件失败',
                        content: rs.message
                    })
                }
                return;
            }

            // 获取文件名称
            const contentDisposition = headers.get('content-disposition');
            if (contentDisposition == null) {
                Modal.error({
                    title: '获取文件名称失败',
                    content: "缺少content-disposition响应头"
                })
                return
            }


            let regExp = new RegExp('filename=(.*)');
            const result = regExp.exec(contentDisposition);
            let filename = result[1];

            filename = decodeURIComponent(filename);
            filename = filename.replaceAll('"', '');
            filename = filename.replace(/^["](.*)["]$/g, '$1')


            const url = window.URL.createObjectURL(new Blob([blob]));
            const link = document.createElement('a');
            link.style.display = 'none';

            link.href = url;
            link.download = decodeURI(filename); // 下载后文件名

            document.body.appendChild(link);
            link.click();

            document.body.removeChild(link); // 下载完成移除元素
            window.URL.revokeObjectURL(url);
        })

    }
}

export const HttpSimpleUtil = {

    get(url, params = null) {
        let config = {
            url,
            method: 'GET',
            params,
        };
        config.showMessage = false
        config.transformData = false
        return request(config)
    },

    post(url, data, params = null) {
        let config = {
            url,
            method: 'POST',
            params,
            data,
        };
        config.showMessage = false
        config.transformData = false
        return request(config)
    },


}
