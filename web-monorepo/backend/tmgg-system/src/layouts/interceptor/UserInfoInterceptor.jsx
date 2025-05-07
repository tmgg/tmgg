import { HttpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";

export  class UserInfoInterceptor{



    preHandle() {
        console.log("登录信息拦截器")
        return new Promise((resolve, reject) => {
            HttpUtil.create().get('/getLoginInfo').then(res => {
                const rs = res.data
                if (rs.success) {
                    SysUtil.setLoginInfo(rs.data)
                    HttpUtil.get('sysDict/tree').then(rs => {
                        SysUtil.setDictInfo(rs)
                        resolve(0)
                    })
                }else {
                    reject(401)
                }
            }).catch(reject)
        });
    }



    getMessage(){
        return '加载用户数据'
    }
}
