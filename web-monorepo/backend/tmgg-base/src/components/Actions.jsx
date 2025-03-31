import React from 'react';
import {Dropdown} from 'antd';
import {PermUtil} from "../system";
import {ArrUtil} from "@tmgg/tmgg-commons-lang";


export class Actions extends React.Component {

    render() {
        const items = this.props.children
        if (items == null || items.length === 0) {
            return
        }

        const list = []
        for (let i = 0; i < items.length; i++) {
            if(!this.checkShow(items[i])){
                continue
            }
            list.push({
                label: items[i]
            })
        }

        if(list.length === 0){
            return
        }

        if(list.length === 1){
            return list[0]
        }

        const defaultItem = list[0];
        ArrUtil.removeAt(list, 0)

        return <>
            <Dropdown.Button menu={{items: list}} size={this.props.size}>
                {defaultItem.label}
            </Dropdown.Button>
        </>
    }


    checkShow(item) {
        const {show,perm} = item.props;
        if (show != null) {
            return show
        }

        if (perm == null) {
            return true;
        }

        return PermUtil.hasPermission(perm)
    }


}
