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




    let finalConfig = Object.assign({},defaultConfig, config);

    const {transformData, showMessage, loadingCallback, ...axiosConfig} = finalConfig

    if(loadingCallback){
        loadingCallback(true)
    }

    if (axiosConfig.url.startsWith("//")) {
        axiosConfig.url = axiosConfig.url.substring(1)
    }


    return new Promise((resolve, reject) => {
        axios(axiosConfig).then(response => {
            if(!transformData){
                resolve(  response);
                return ;
            }

            const body = response.data;
            const {success,message, data} = body;

            if(success == null){ // 返回结果没有success标志，则原样
                resolve(  body);
                return
            }


            if(showMessage){
                if(success){
                    if(message){
                        Message.success(message)
                    }
                }else {
                    Modal.error({
                        title: '服务出错',
                        content: message
                    })
                }
            }

            if(success){
                resolve(data)
            }else {
                reject(message)
            }


        }).catch(e => {
            let msg = e;
            if(e.response && e.response.data){
                const rs = e.response.data
                msg = rs.message
            }
            if(msg == null && e.message){
                msg = e.message
            }

            Modal.error({
                title: '捕获到服务出错',
                content: msg
            })
            reject(e)
        }).finally(() => {
            if(loadingCallback){
                loadingCallback(false)
            }
        })
    })

}


export const HttpUtil = {
    request,

    get(url, params = null) {
        return request({
            url,
            method:'GET',
            params,
        })
    },

    post(url, data, params = null) {
        return request({  url,
            method:'POST',
            params,
            data
        })
    },

    postForm(url, data) {
        return request({  url,
            method:'POST',
            data,
            headers:{
               'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
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

        return this.get(url, {...data,...pageParams}).then(convertToProTableData)
    },
    downloadFile(url, params) {
        let config = {
            url,
            params,
            responseType: 'blob',
            transformData: false
        };
        request(config).then(res => {
            const {data: blob, headers} = res
            if(blob.type === 'application/json'){// 可能是错误了，否则不应该返回json
                const reader = new FileReader();
                reader.readAsText(blob, 'utf-8');
                reader.onload = function (e) {
                    console.info(reader.result);
                    Modal.error({
                        title: '下载文件失败',
                        content: reader.result
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
