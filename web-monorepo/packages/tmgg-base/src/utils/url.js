
export const UrlUtil = {
    /**
     * 获取url的参数, 如果不传参数，则使用当前路径
     * @param url 字符串
     */
    params(url= null) {
        if (!url) {
            url = location.href
        }

        const urlObj = new URL(url);
        const params = new URLSearchParams(urlObj.search);

        const result = {}
        for (const [key, value] of params.entries()) {
            result[key] = value;
        }

        return result
    },

    /**
     * 去掉参数的基础url
     * @param url
     */
    baseUrl(url) {
        if (!url) {
            return null
        }
        return new URL(url).origin
    },


    /**
     * 将参数对象转换为 url中的
     * @param params
     */
    paramsToSearch(params) {
        if (!params) {
            return "";
        }
        const buffer = []
        for (let k in params) {
            let v = params[k]
            buffer.push(k + '=' + v);
        }
        return buffer.join('&')
    },

    replaceParam(url, key, value) {
        const p = this.params(url)
        p[key] = value;
        return this.baseUrl(url) + '?' + this.paramsToSearch(p);
    }

}
