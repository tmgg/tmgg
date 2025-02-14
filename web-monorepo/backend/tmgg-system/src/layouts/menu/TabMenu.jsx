import React from "react";
import {history, Outlet} from "umi";
import {Tabs} from "antd";
import KeepAlive from "react-activation";

export default class extends React.Component {

    onClick = key => {
        const item = this.findItem(key);

        history.push(item.path)
    }

    findItem = key => {
        const {items} = this.props

        const item = items.find(item => item.key === key)
        return item;
    };

    remove = key => {
        const item = this.findItem(key)
        this.props.onTabRemove(item)
        return false
    }

    render() {
        const {items, pathname} = this.props
        if (items.length === 0) {
            return
        }

        let activeKey = null

        const menuItems = items.map(item => {
            const {label, key, path} = item
            if (path === this.props.path) {
                activeKey = key
            }


            return {
                label, key, path, children:  <Outlet/>
            }
        })


        return <>
            <Tabs hideAdd
                  size='small'
                  type='editable-card'
                  style={{padding: 8}}
                  items={menuItems}
                  activeKey={activeKey}
                  onChange={this.onClick}
                  onEdit={this.remove}
                  destroyInactiveTabPane={false}>
            </Tabs>
        </>

    }

}
