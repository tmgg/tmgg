import React from "react";
import {getRoutesMap, PageUtil} from "@tmgg/tmgg-base";
import {Outlet, withRouter} from "umi";
import {Tabs} from "antd";
import {UrlUtil} from "@tmgg/tmgg-commons-lang";

class MyOutlet extends React.Component {


    render() {
        const {location,params} =this.props
        const {pathname,search} = location

        // 判断是否动态路径
        let routePath = pathname.substring(1)
        // 可能是动态路径， 如 user/:id
        for (let paramKey in params) {
            let paramValue = params[paramKey]
            routePath = routePath.replace(paramValue, ":" + paramKey)
        }
        const map = getRoutesMap()
        const componentType = map[routePath];
        if (componentType) {
            const urlParams =UrlUtil.getParams(search)
            const finalParam = {...params, ...urlParams}
            return React.createElement(componentType, {location, params:finalParam});
        }
    }

}

export default withRouter(MyOutlet)
