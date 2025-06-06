import React from "react";
import {PageUtil} from "@tmgg/tmgg-base";
import {withRouter} from "umi";
import {Spin, Tabs} from "antd";
import {PageRender} from "../PageRender";

class TabPageRender extends React.Component {

    state = {
        active: null,
        urlLabelMap: {},

        tabs: [],

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

    onUrlChange = url => {
        const {location} = this.props

        const {pathname,search} = location
        let {tabs} = this.state

        const old = tabs.find(t=>t.key === url)
        if(old == null){
            const cmp =  <PageRender  pathname={pathname} search={search}/>
            let label = this.getLabel(pathname);
            tabs.push({
                key: url,
                label: label,
                children: cmp
            });
            this.setState({tabs:[...tabs]})
        }

        this.setState({active:url})
    };

    getLabel(path) {
        if(path === '/'){
            return '首页'
        }
        let label = PageUtil.currentLabel();

        if (!label) {
            const pathLabelMap = this.props.pathLabelMap
             label = pathLabelMap[path]
        }

        return label;
    }


    render() {
        let {tabs} = this.state
        if(tabs.length === 0){
            return  null
        }

        return <>
            <Tabs
                items={tabs}
                activeKey={this.state.active}
                onChange={this.onChange}
                onEdit={this.onRemove}

                hideAdd
                size='small'
                type='editable-card'
                style={{background: 'white'}}
                rootClassName='tmgg-layout-tabs'

                onTabClick={this.onTabClick}
            >
            </Tabs>
        </>
    }

    // 上次点击tab事件
    lastTabClickTime = 0
    onTabClick = (key, event) => {
        let now = new Date().getTime();
        let doubleClick = now - this.lastTabClickTime < 300;
        if(doubleClick){
            const tabs = this.state.tabs;
            const tab = tabs.find(t=>t.key === key)
            const old = tab.children
            tab.children = '刷新中...'
            this.setState({tabs},()=>{
                tab.children = old
                this.setState({tabs})
            })
        }

        this.lastTabClickTime = now
    };

    onChange = url => {
        if(url !== this.state.active){
            this.props.history.push(url)
        }
    };

    onRemove = url => {
        let {tabs} = this.state
        tabs = tabs.filter(t=>t.key !== url)

        this.setState({tabs})
        if(tabs.length > 0){
            this.setState({active: tabs[tabs.length -1].key})
        }
    };

    getUrl = props => {
        const {params,location} = props
        const {pathname, search} = location
        return  pathname + search;
    }

}



// 让组件有路由相关的参数，如 this.props.location
export default withRouter(TabPageRender)
