import React from "react";
import {Button, Space} from "antd";
import hutool from "@moon-cn/hutool";
import {sys} from "../../common";
import {history} from "umi";
import {CloseOutlined} from "@ant-design/icons";

export default class extends React.Component {


    render() {
        const {items, pathname} = this.props
        return <div>
            <Space size={4}>
            {items.map(item=>{
                return <Button type={pathname === item.path ? 'primary':'default'}
                               size={"middle"}
                               key={item.key}
                               icon={item.icon}
                               onClick={() => history.push(item.path)}
                               >{item.label} <CloseOutlined /> </Button>
            })}
            </Space>
        </div>
    }


}
