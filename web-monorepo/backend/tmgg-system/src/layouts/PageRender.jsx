import {useAppData} from "umi";
import React from "react";
import {Result} from "antd";
import {matchRoutes} from "umi";

/**
 * 通过指定 pathname 渲染页面
 * @param props
 * @returns {React.JSX.Element|*}
 * @constructor
 */
export default function PageRender(props) {
    let {pathname} = props
    const appData = useAppData()


    const mathArr= matchRoutes(appData.clientRoutes,pathname)
    if(mathArr!=null){
        const mathResult = mathArr[mathArr.length-1].route
        return mathResult.element;
    }

    // 如果实在找不到页面组件，则适用自带
    return <Result
        status={404}
        title='页面不存在！'
        subTitle={<div>路由地址：{pathname}</div>}
    />
}
