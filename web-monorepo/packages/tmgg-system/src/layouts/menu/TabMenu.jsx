import React from "react";
import {Button, Space} from "antd";
import {history} from "umi";
import {CloseOutlined} from "@ant-design/icons";

export default class extends React.Component {


    render() {
        const {items, pathname} = this.props
        if(items.length === 0){
            return
        }
        return <div className='tabs-nav'>
            {items.map(item=>{
                let active = pathname === item.path;
                let key = item.key;
                let icon = item.icon;
                let path = item.path;
                let label = item.label

                let className = 'tab ' + (active ? 'active':'');
                return <div className={className} >
                    <div className='icon'  >     {icon}
                    </div>
                    <div className='btn'  onClick={() => history.push(path)}>

                        {label}
                    </div>
                    <div className='remove' onClick={()=>this.props.onTabRemove(item)}>
                        <CloseOutlined />
                    </div>
                </div>

            })}
        </div>
    }


}
