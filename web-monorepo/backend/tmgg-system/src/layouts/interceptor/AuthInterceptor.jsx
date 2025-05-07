import React from "react";
import {HttpUtil, PageLoading, PageUtil, SysUtil} from "@tmgg/tmgg-base";
import {history} from "umi";
import {Button, Result} from "antd";

export  class  AuthInterceptor {

    preHandle(){
        return new Promise((resolve, reject) => {
            HttpUtil.get('/checkLogin', null, false).then(rs => {
                console.log("checkLogin", rs)
                const {isLogin,needUpdatePwd} =rs

                if(isLogin && !needUpdatePwd){
                    resolve(0)
                    return
                }

                if(needUpdatePwd ){
                    PageUtil.open('/userCenter/ChangePassword','修改密码')
                    resolve(0)
                    return;
                }
               // 缓存路径
                let pathname = PageUtil.currentPathname();
                localStorage.setItem("login_redirect_path", pathname)
                resolve(401)
            }).catch(()=>resolve(1))
        })
    }

    getMessage(){
        return '登录有效性检测'
    }
}
