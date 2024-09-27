import React from "react";
import {history} from "umi";
import {CloseOutlined} from "@ant-design/icons";
import {Menu, Tabs} from "antd";

export default class extends React.Component {

    onClick = e=>{
        const {key} = e;
        const item = this.findItem(key);

        history.push(item.path)
    }

    findItem = key => {
        const {items} = this.props

        const item = items.find(item => item.key === key)
        return item;
    };

    remove = e=>{
        e.preventDefault()
        e.stopPropagation()
        const key = e.currentTarget.dataset.key
        const item = this.findItem(key)
        this.props.onTabRemove(item)
        return false
    }

    render() {
        const {items, pathname} = this.props
        if(items.length === 0){
            return
        }

        let activeKey = null

        const menuItems = items.map(item=>{
            const {label, key, path} = item
            if(path === pathname){
                activeKey = key
            }

            return {label,key,path, extra: <CloseOutlined data-key={key} onClick={this.remove} /> }
        })


        return <Menu items={menuItems}
                     style={{lineHeight:'36px'}}
                     mode="horizontal"
                     onClick={this.onClick}
                     selectedKeys={[activeKey]}
        >

        </Menu>

    }

}
