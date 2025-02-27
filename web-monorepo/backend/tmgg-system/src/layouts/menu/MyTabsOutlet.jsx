import React from "react";
import {getRoutesMap, PageUtil} from "@tmgg/tmgg-base";
import {Outlet, withRouter} from "umi";
import {Tabs} from "antd";
import {UrlUtil} from "@tmgg/tmgg-commons-lang";

class MyTabsOutlet extends React.Component {

    state = {
        cache: {},
        active: null,
        urlLabelMap: {},

        tabs:[]
    }

    componentDidMount() {
        const url = this.getUrl(this.props)
        this.onUrlChange(url);
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        const url = this.getUrl(this.props)
        const pUrl = this.getUrl(prevProps)

        if (url !== pUrl) {
            this.onUrlChange(url);
        }
    }

    onUrlChange(url) {
        let {cache,tabs} = this.state;
        let {location, params} = this.props;
        if (cache[url] == null) {
            cache[url] = {location, params}
            this.setState({cache})

            let label = this.getLabel(location.pathname);
            tabs.push({
                key: url,
                label: label,
               children: this.getComponent(url)
            });
            this.setState({tabs})
        }
        this.setState({active: url})
    }

    getLabel(path) {
        const pathLabelMap = this.props.pathLabelMap
        let label = pathLabelMap[path]
        if (!label) {
            label = PageUtil.currentLabel();
        }

        return label;
    }


    render() {
        let {tabs} = this.state
        let items =tabs.map(tab=>{
            return {...tab}
        })


        return <>
            <Tabs
            items={items }
            activeKey={this.state.active}
            onChange={this.onChange}
            onEdit={this.onRemove}

            hideAdd
            size='small'
            type='editable-card'
            style={{background: 'white'}}
            destroyInactiveTabPane={false}
        >
        </Tabs></>
    }
    onChange = url => {
        this.props.history.push(url)
    };

    onRemove = url => {
        const {cache} = this.state
        delete cache[url]
        const urls = Object.keys(cache)

        const newUrl = urls[urls.length -1]
        this.props.history.push(newUrl || '/')

        this.setState({cache})
    };

    getUrl = props => {
        const {pathname, search} = props.location
        let url = pathname + search;
        return url;
    }
    getComponent = (id) => {
        const {location, params} = this.state.cache[id]
        const {pathname,search} = location

        const map = getRoutesMap()
        if (pathname == null || pathname.length === 0) {
            return '首页'
        }

        // 判断是否动态路径
        let routePath = pathname.substring(1)
        // 可能是动态路径， 如 user/:id
        for (let paramKey in params) {
            let paramValue = params[paramKey]
            routePath = routePath.replace(paramValue, ":" + paramKey)
        }

        const componentType = map[routePath];
        if (componentType) {
            const urlParams =UrlUtil.getParams(search)
            const finalParam = {...params, ...urlParams}
            return React.createElement(componentType, {location, params:finalParam});
        }

        // 如果实在找不到页面组件，则适用自带
        return <Outlet/>
    }
}

// 让组件有路由相关的参数，如 this.props.location
export default withRouter(MyTabsOutlet)
