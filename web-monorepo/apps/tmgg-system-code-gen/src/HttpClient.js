import axios from "axios";

const AUTH_STORE_KEY = 'jwt';

axios.defaults.withCredentials = true
axios.defaults.baseURL = '/'
axios.interceptors.request.use(
    config => {
        let item = localStorage.getItem( AUTH_STORE_KEY);
        if(item){
            config.headers['Authorization'] = item;
        }

        return config;
    }
);

axios.interceptors.response.use(res => {
    return res.data;
})


export default class {

     static get(url, params) {
        return axios.get(url, {params})
    }

    static post(url, data) {
        return axios.post(url, data)
    }

  // 获得分页数据
  static getPageableData = (url, param, sort, method = 'GET' /* GET | POST */) => {
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

    const { current, pageNumber, pageNo, pageSize, orderBy, ...data } = param;
    const params = { pageNumber, pageNo, pageSize, orderBy };

    // @ts-ignore
    return axios[method.toLowerCase()](url, {params}, data).then((result) => {
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



