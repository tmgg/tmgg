import {useAppData} from "umi";
import React from "react";
import {Result} from "antd";
import {matchRoutes} from "umi";

export default function MyPureOutlet(props) {
    let {pathname} = props
    const appData = useAppData()
    if(pathname === '/'){
        pathname = '/index'
    }


    const mathArr= matchRoutes(appData.clientRoutes,pathname)
    if(mathArr.length > 0){
        const mathResult = mathArr[mathArr.length-1].route
        return mathResult.element;
    }

    // 如果实在找不到页面组件，则适用自带
    return <Result
        status={404}
        title='页面未找到'
        subTitle={<div>路由地址：{pathname}</div>}
    />
}
