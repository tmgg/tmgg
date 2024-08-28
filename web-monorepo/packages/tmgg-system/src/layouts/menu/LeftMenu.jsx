import React from "react";
import {Button, Menu, Skeleton, Space} from "antd";
import {TreeUtil} from "../../common";
import * as Icons from "@ant-design/icons";
import {http} from "@tmgg/tmgg-base";
import {history} from "umi";

export default class extends React.Component {

    state = {
        menuList: [],
        defaultOpenKeys: undefined,
        menuLoading: true,

        menuMap: {} // 缓存 path和key，方便快速查询
    }


    componentDidMount() {

        http.get('appMenuTree').then(rs => {
            const list = rs.data;
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

                map[item.path] = item.id
            })

            this.setState({menuLoading:false})
            this.setState({menuList: list, menuMap:map})

            if(list.length >0){
                this.setState({defaultOpenKeys:[list[0].id]})
            }


        })
    }


    render() {
        let {menuLoading, menuMap} = this.state;

        if(menuLoading){
            return <Skeleton></Skeleton>
        }
        const {pathname} = this.props

        const key = menuMap[pathname]

        return    <Menu items={this.state.menuList}
                  theme='dark'
                  mode="inline"
                  defaultOpenKeys={this.state.defaultOpenKeys}
                        onClick={({item})=>{
                            history.push(item.props.path)
                  }}
                        selectedKeys={[key]}
            >
            </Menu>
    }
}
