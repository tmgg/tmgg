import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import {HttpUtil} from "@tmgg/tmgg-app-base";
import {Cell, InfiniteScroll, PullToRefresh, VirtualList} from "@antmjs/vantui";


export default class extends Component {

  state = {
    data: []
  }
  InfiniteScrollInstance = React.createRef()
  VirtualListInstance = React.createRef()


  componentDidMount() {

  }

  loadData = async () => {
    return await HttpUtil.pageData('sysConfig/page', {})
  }

  loadMore = async () => {
    return new Promise(async (resolve) => {
      const {content, last} = await this.loadData()
      const newData = this.state.data.concat(content)
      this.setState({data: newData})
      resolve('complete')
    })
  }

  onRefresh = () => {
    return new Promise(async (resolve) => {
      this.InfiniteScrollInstance.current?.reset()
      const {content, last} = await this.loadData()
      await this.VirtualListInstance.current?.reset()
      this.setState({data: content})
      resolve(undefined)
    })
  }


  render() {
    return <View>
      <PullToRefresh onRefresh={this.onRefresh}>
        <VirtualList
          style={{padding: 10, boxSizing: 'border-box'}}
          height="calc(100vh - 135px)"
          dataSource={this.state.data}
          showCount={3}
          ref={this.VirtualListInstance}
          footer={
            <InfiniteScroll
              parentClassName="van-virtual-list"
              loadMore={this.loadMore}
              ref={this.InfiniteScrollInstance}
            />
          }
          ItemRender={({item, id}) => {
            let {value, defaultValue, name} = item;
            if (value == null) {
              value = defaultValue;
            }
            return <View key={item.id} id={id}>
              <Cell title={name} value={value} border ></Cell>
            </View>
          }}
        />
      </PullToRefresh>
    </View>;

  }


}
