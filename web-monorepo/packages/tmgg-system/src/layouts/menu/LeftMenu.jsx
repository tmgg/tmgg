import React from "react";
import {Badge, Button, Menu, Skeleton, Space} from "antd";
import {TreeUtil} from "@tmgg/tmgg-base";
import * as Icons from "@ant-design/icons";
import {http} from "@tmgg/tmgg-base";
import {history} from "umi";

export default class extends React.Component {

    state = {
        menuList: [],
        defaultOpenKeys: undefined,
        menuLoading: true,

        pathMap: {} // 缓存 path和key，方便快速查询
    }


    componentDidMount() {

        httpUtil.get('menuTree').then(list => {
            const map = {}
            // 设置icon
            TreeUtil.every(list, (item) => {
                let IconType = Icons[ item.icon || 'SmileOutlined'];
                item.icon = <IconType style={{fontSize: 12}}/>

                if (item.path) {
                    if (item.iframe) {
                        item.iframePath = item.path;

                        // pro layout的bug， 如果http开头的，会直接打开新窗口
                        if (item.path.startsWith('http')) {
                            item.path = '/' + item.path;
                        }
                    }
                }

                map[item.path] = item
            })

            this.setState({menuLoading:false})
            this.setState({menuList: list, pathMap:map})

            if(list.length >0){
                this.setState({defaultOpenKeys:[list[0].id]})
            }


        })
    }


    render() {
        let {menuLoading, pathMap} = this.state;

        if(menuLoading){
            return <Skeleton></Skeleton>
        }
        const {pathname} = this.props

        const curMenu = pathMap[pathname]

        return    <Menu items={this.state.menuList}
                  theme='dark'
                  mode="inline"
                  defaultOpenKeys={this.state.defaultOpenKeys}
                        onClick={({key,item})=>{
                            let {path} = item.props;
                            let clickMenu = pathMap[path]
                            history.push(path)
                            this.props.onSelect( key, path,clickMenu.label, clickMenu.icon)
                  }}
                        selectedKeys={[curMenu?.id]}
            >
            </Menu>
    }

    renderBadge(item){
      return   <Badge count={item.badge} size="small">
            <span style={{display: 'inline-block', width: 10}}></span>
        </Badge>
    }
}
