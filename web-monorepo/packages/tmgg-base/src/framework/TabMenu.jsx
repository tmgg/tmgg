import React from "react";
import {Button, Space} from "antd";

export default class extends React.Component {


    render() {
        const {items} = this.props
        return <div>
            <Space size={4}>
            {items.map(item=>{
                return <Button size={"small"} key={item.key} icon={item.icon} onClick={()=>this.props.onClick(item)}>{item.name || item.label}</Button>
            })}
            </Space>
        </div>
    }
}
