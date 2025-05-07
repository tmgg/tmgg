import React from "react";
import {HttpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";

export  class SiteInfoInterceptor {




    preHandle(){
        return new Promise((resolve, reject) => {
            HttpUtil.get("/site-info").then(rs=>{
                SysUtil.setSiteInfo(rs)
                resolve(0)
            }).catch(reject)
        })
    }

    getMessage(){
        return '加载站点数据'
    }



}
