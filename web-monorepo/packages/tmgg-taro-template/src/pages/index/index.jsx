import { View } from '@tarojs/components'
import { Button } from '@antmjs/vantui'
import './index.less'
import React, {Component} from "react";
import {HttpUtil} from "@tmgg/tmgg-taro";



export default class Index extends React.Component {

  state={
    siteInfo: {}
  }

  componentDidMount () {
    HttpUtil.get('/site-info').then(rs=>{
      this.setState({siteInfo:rs.data})
    })

  }

  componentWillUnmount () { }

  componentDidShow () { }

  componentDidHide () { }

  render () {
    return (
      <View className='index'>
        <View><Button type='primary'>Hello world!</Button></View>
        <View>上面的按钮的颜色已经通过全局主题重写覆盖了，参见src/style/index.less</View>

        siteInfo:{JSON.stringify( this.state.siteInfo)}

      </View>
    )
  }
}
