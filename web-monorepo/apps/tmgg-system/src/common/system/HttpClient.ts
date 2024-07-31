/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import {extend} from 'umi-request';
import {message} from 'antd';
import moment from 'moment';
import {history} from 'umi';
import {ObjectUtil} from '../utils';
import {MessageUtil} from './MessageUtil';
import {sys, SysConfig} from "./SysConfig";

export interface AjaxResult {
  success: boolean;
  message: String;
  data: any;
}

const DEFAULT_MESSAGE = '未知错误';

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新增或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新增或修改数据的操作。',
  401: '请登录',
  403: '权限不足',
  404: '接口未定义',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

const errorHandler = (error: any) => {
  console.log('进入异常处理器', error);

  let status = error?.response?.status;
  let code = error?.data?.code || status;

  if (code == 401) {
    // 直接跳到登录界面，不提示信息
    console.log('未登录，跳转登录页面', error);
    localStorage.clear();
    // @ts-ignore
    let loginUrl = SysConfig.getLoginUrl();
    console.log('登录url', loginUrl)
    history.push(loginUrl);

    return {success: false, message: '请先登录'};
  }

  const message = getErrorMessage(error);

  return {success: false, message};
};

function getErrorMessage(err: any) {
  if (err == null) {
    return DEFAULT_MESSAGE;
  }
  if (err.name == 'TypeError') {
    return err.message;
  }
  if (err.data) {
    if (err.data.message) {
      return err.data.message;
    }
  }

  // 服务器 ajaxResult
  if (err.data && err.data.message) {
    return err.data.message;
  }

  if (err.response && err.response.status) {
    // @ts-ignore
    const msg = codeMessage[err.response.status];
    if (msg) {
      return msg;
    }
  }

  if (err.message) {
    return err.message;
  }

  return DEFAULT_MESSAGE;
}

// 设置登陆header
let requestHandler = (url: string, options: any) => {
  const headers = getHeader();
  if (headers) {
    options.headers = Object.assign(options.headers, headers);
  }

  let isFullUrl = url.startsWith('http') || url.startsWith('https');
  if (!isFullUrl) {
    // 防止双斜杠出现
    if (SysConfig.getServerUrl().endsWith('/') && url.startsWith('/')) {
      url = url.substr(1);
    }
    // 添加请求前缀
    url = SysConfig.getServerUrl() + url;
  }

  let info = {url: url, options: options};
  return info;
};

const filterParam = (values: any) => {
  if (!values) {
    return null;
  }
  for (let item in values) {
    let obj = values[item];
    if (!obj) {
      continue;
    }
    if (obj.constructor === Object) {
      for (let key in obj) {
        if (obj[key] === '') {
          delete obj[key];
        }
      }
      if (Object.keys(obj).length == 0) {
        values[item] = null;
      }
    } else if (obj instanceof moment) {
      // @ts-ignore
      values[item] = obj._i;
    } else if (ObjectUtil.isString(obj)) {
      values[item] = obj.trim();
    }
  }
  return values;
};

const MyRequest = extend({
  errorHandler,
  getResponse: false,
  credentials: 'include',
});

MyRequest.interceptors.request.use(requestHandler);


/**
 *
 * @param isGet
 * @param url
 * @param params
 * @returns {Promise<unknown>}
 */
function _send(method: string, url: string, params: any, data: any,
               options: any, doneCallback: any): Promise<any> {
  const defaultOptions = {
    trim: true, // 是否将字符串trim
  };
  options = Object.assign(defaultOptions, options);

  if (method == 'GET') {
    // 合并 params 和data，
    params = Object.assign({}, params, data);
    data = null;
  }

  if (options.trim) {
    params = filterParam(params);
  }

  return new Promise((resolve, reject) => {
    const options = {
      method,
      params,
      data,
    };

    MyRequest(url, options)
      .then((rs) => {
        if (rs.success === false) {
          if (rs.message == 'NEED_CHANGE_PASSWORD') {
            history.push('/system/userCenter/changePassword');
            doneCallback && doneCallback(false);
            reject('请修改密码');
            return;
          } else {
            MessageUtil.notificationError(rs.message);
          }

          doneCallback && doneCallback(false);
          reject(rs.message);
          return;
        }

        if (rs.success) {
          doneCallback && doneCallback(true);
          resolve(rs);
        } else {
          doneCallback && doneCallback(false);
          reject(rs);
        }
      })
      .catch((e) => {
        message.error(e);
        doneCallback && doneCallback(false);
        reject(e);
      });
  });
}
// 存储在localStore中的 key，直接放到请求头中
const LOCALSTORAGE_HEADER_PREFIX = "header:";

function getHeader() {
  const headers = sys.getHeader();

  for(let i = 0; i < localStorage.length ;i++){
    let key = localStorage.key(i);
    // @ts-ignore
    if(key.startsWith(LOCALSTORAGE_HEADER_PREFIX)){
      // @ts-ignore
      const value = localStorage.getItem(key);

      // @ts-ignore
      headers[key.substring(LOCALSTORAGE_HEADER_PREFIX.length)] = value;
    }
  }


  return headers;
}



export class HttpClient {

  /**
   * @deprecated
   */
  static getServerUrl() {
    return SysConfig.getServerUrl();
  }

  /**
   * @deprecated
   */
  static getToken = ()=>sys.getToken()

  /**
   * @deprecated
   */
  static getHeader = ()=>sys.getHeader();

  static setGlobalHeader = function (key:string, value:any){
    localStorage.setItem(LOCALSTORAGE_HEADER_PREFIX + key, value)
  }
  static getGlobalHeader = function (key:string){
    key = LOCALSTORAGE_HEADER_PREFIX + key;
    return localStorage.getItem(key)
  }

  static request = (method: string, url: string, param: any, doneCallback: any) => {
    if (method == 'GET') {
      return this.get(url, param, doneCallback);
    }
    if (method == 'POST') {
      return this.post(url, param, doneCallback);
    }
  };
  static get = (url: string, params: any = {}, doneCallback: any = null): Promise<AjaxResult> => {
    return _send('GET', url, params, null, null, doneCallback);
  };
  static post = (url: string, params: any = {}, doneCallback: any = null): Promise<AjaxResult> => {
    return _send('POST', url, null, params, null, doneCallback);
  };

  static postForm = (url: string, params: any = {}, doneCallback: any = null): Promise<AjaxResult> => {
    return _send('POST', url, null, params, {requestType: 'form'}, doneCallback);
  };

  static getBlob = (url: string) => {
    return MyRequest(url, {
      method: 'GET',
      responseType: 'blob',
      parseResponse: false,
    });
  };
  static downloadFile = (url: string, params: any, method = 'GET', data: any) => {
    const hide = message.loading('下载中...', 0)

    return new Promise((resolve, reject) => {
      params = filterParam(params);

      MyRequest(url, {
        method: method,
        params,
        data,
        responseType: 'blob',
        parseResponse: false,
      }).then((response: any) => {
        console.log('下载数据结束', response);


        // @ts-ignore
        response.blob().then((blob) => {
          {
            console.log("解析blob结束")

            var contentDisposition = response.headers.get('content-disposition');
            if (contentDisposition == null) {
              hide();
              message.error('获取文件信息失败');
              reject(null)
              return
            }


            let regExp = new RegExp('filename=(.*)');
            // @ts-ignore
            var result = regExp.exec(contentDisposition);

            // @ts-ignore
            var filename = result[1];

            filename = decodeURIComponent(filename);
            filename = filename.replaceAll('"', '');

            var href = window.URL.createObjectURL(blob); // 创建下载的链接
            var reg = /^["](.*)["]$/g;

            var a = document.createElement('a');
            a.style.display = 'none';
            a.href = href;
            a.download = decodeURI(filename.replace(reg, '$1')); // 下载后文件名
            document.body.appendChild(a);
            a.click(); // 点击下载
            document.body.removeChild(a); // 下载完成移除元素
            window.URL.revokeObjectURL(href);
            hide();
            resolve(null)
          }
        }).catch((err: any) => {
          hide();
          reject(err)
        });
      }).catch((err: any) => {
        hide()
        reject(err)
      }); //end myrequet
    })
// end return

  };

  // 获得分页数据
  static getPageableData = (url: string, param: any, sort: any, method = 'GET' /* GET | POST */) => {
    // 分页参数
    param.pageNo = param.current;
    if (sort) {
      let keys = Object.keys(sort);
      if (keys.length > 0) {
        let key = keys[0];
        let dir = sort[key] == 'ascend' ? 'asc' : 'desc';
        param.orderBy = key + ',' + dir;
      }
    }

    const {current, pageNumber, pageNo, pageSize, orderBy, ...data} = param;
    const params = {pageNumber, pageNo, pageSize, orderBy};

    // @ts-ignore
    return _send(method, url, params, data).then((result) => {
      // @ts-ignore
      const page = result.data;

      // 按pro table 的格式修改数据结构
      return {
        data: page.content,
        success: true,
        total: parseInt(page.totalElements),

        // PageExt 属性
        title: page.title,
        footer: page.footer,

        // 原始数据
        rawData: result,
      };
    });
  };
}
